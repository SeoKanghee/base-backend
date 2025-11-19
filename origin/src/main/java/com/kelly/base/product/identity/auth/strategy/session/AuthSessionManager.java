package com.kelly.base.product.identity.auth.strategy.session;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.product.identity.response.IdentityResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthSessionManager {
    private final SessionRegistry sessionRegistry;

    /**
     * 세션 등록 및 갱신 처리
     * 중복 세션 체크, 세션 생성/갱신, SecurityContext 설정을 수행
     *
     * @param authentication 계정 인증 정보
     * @param servletRequest HTTP 요청 정보
     * @param isForce        기존 세션을 무시하고 강제 로그인할지 여부
     * @author kelly
     */
    public void dispatchSession(final Authentication authentication, final HttpServletRequest servletRequest,
                                final boolean isForce) {
        // 1. 현재 세션 ID 추출
        final String currentSessionId = getCurrentSessionId(servletRequest);

        // 2. 중복 세션 처리
        checkDuplicateSession(authentication, currentSessionId, isForce);

        // 3. 세션 등록/갱신 처리
        updateSession(servletRequest, authentication, currentSessionId);
    }

    String getCurrentSessionId(final HttpServletRequest servletRequest) {
        // 요청 정보로 부터 sessionId 추출 -> session 정보가 없으면 null 반환
        final HttpSession currentSession = servletRequest.getSession(false);
        return currentSession != null ? currentSession.getId() : null;
    }

    void checkDuplicateSession(final Authentication authentication, final String currentSessionId,
                               final boolean isForce) {
        final List<SessionInformation> relatedSessions = getRelatedSessions(authentication, currentSessionId);

        // 다른 세션이 존재하면 force 옵션에 따른 처리
        if (!relatedSessions.isEmpty()) {
            log.debug("related session size : {}", relatedSessions.size());

            final String loginId = authentication.getName();
            if (isForce) {
                // 기존 세션 전부 삭제 ( 현재 구조에서는 세션은 1개만 존재해야 함 )
                relatedSessions.forEach(SessionInformation::expireNow);
                log.info("force login: expired other session for [{}]", loginId);
            } else {
                // 로그인 실패 ( exception 처리 )
                log.error("login denied: other session found for [{}]", loginId);
                throw new CommonRuntimeException(IdentityResultCode.ALREADY_LOGIN);
            }
        }
    }

    private List<SessionInformation> getRelatedSessions(final Authentication authentication,
                                                        final String currentSessionId) {
        // 관련 세션 목록 반환 ( 현재 세션은 제외 )
        final List<SessionInformation> existingSessions
                = sessionRegistry.getAllSessions(authentication.getPrincipal(), false);
        return existingSessions.stream()
                               .filter(s -> !s.getSessionId().equals(currentSessionId))
                               .toList();
    }

    void updateSession(final HttpServletRequest servletRequest, final Authentication authentication,
                       final String currentSessionId) {
        // authentication 을 securityContext 로 wrapping 해서 session attribute 에 추가
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        final HttpSession httpSession = servletRequest.getSession(true);    // 세션이 없으면 신규 생성
        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // SessionRegistry 등록/갱신
        final String loginId = authentication.getName();
        if (isNewSession(currentSessionId, httpSession.getId())) {
            registerNewSession(authentication, httpSession, loginId);
        } else {
            refreshSessions(httpSession, currentSessionId, loginId);
        }
    }

    private boolean isNewSession(final String prevSessionId, final String justNowSessionId) {
        // getCurrentSessionId 와 updateSession 처리 사이에 세션 만료가 발생할 경우 문제가 될 수 있음
        // 타이밍 이슈 방어를 위해 sessionId 가 동일한지 확인
        return prevSessionId == null || !prevSessionId.equals(justNowSessionId);
    }

    private void registerNewSession(final Authentication authentication, final HttpSession httpSession,
                                    final String loginId) {
        // 새로운 세션 등록
        sessionRegistry.registerNewSession(httpSession.getId(), authentication.getPrincipal());
        log.debug("new session registered for [{}] - sessionId: {}", loginId, httpSession.getId());
    }

    private void refreshSessions(final HttpSession httpSession, final String currentSessionId, final String loginId) {
        // 세션 업데이트
        SessionInformation sessionInfo = sessionRegistry.getSessionInformation(currentSessionId);
        if (sessionInfo != null) {
            sessionInfo.refreshLastRequest();   // 타임스탬프 갱신
            log.debug("session refreshed for [{}] - sessionId: {}", loginId, httpSession.getId());
        }
    }
}

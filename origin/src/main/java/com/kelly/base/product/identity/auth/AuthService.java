package com.kelly.base.product.identity.auth;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.interfaces.IResultCode;
import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.auth.dto.PostLoginRequest;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.repository.AccountRepository;
import com.kelly.base.product.identity.response.IdentityResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final SessionRegistry sessionRegistry;

    private final AccountRepository accountRepository;

    @Transactional
    public CommonResponse<Void> login(@NonNull final PostLoginRequest loginRequest, HttpServletRequest servletRequest) {
        log.debug("login attempt for user: {}", loginRequest);

        try {
            // 1. 인증 시도 -> 인증 실패할 경우 exception 발생
            Authentication authentication = dispatchAuthentication(loginRequest);

            // 2. 인증 성공 처리 : 200
            return handleSuccessfulLogin(authentication, loginRequest, servletRequest);
        } catch (LockedException e) {
            // 계정이 잠겨 있음 : 403
            log.error("account locked: {}", loginRequest.loginId());
            throw new CommonRuntimeException(IdentityResultCode.ACCOUNT_LOCKED);
        } catch (DisabledException e) {
            // 계정이 비활성화 되어 있음 : 403
            log.error("account disabled: {}", loginRequest.loginId());
            throw new CommonRuntimeException(IdentityResultCode.ACCOUNT_DISABLED);
        } catch (BadCredentialsException e) {
            // 인증 실패 ( 비밀번호 틀림 ) : 401
            handleFailedLogin(loginRequest.loginId());
            log.error("invalid credentials for user: {}", loginRequest.loginId());
            throw new CommonRuntimeException(IdentityResultCode.BAD_CREDENTIAL);
        }
    }

    private Authentication dispatchAuthentication(final PostLoginRequest loginRequest) {
        // AuthenticationManager 를 통해 계정 정보 확인
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.loginId(), loginRequest.password())
        );
    }

    CommonResponse<Void> handleSuccessfulLogin(
            final Authentication authentication, final PostLoginRequest loginRequest,
            HttpServletRequest servletRequest
    ) {
        final String loginId = loginRequest.loginId();
        final IResultCode resultCode = getLoginResultCode(loginId);
        log.info("result code: {}, loginId: {}", resultCode, loginId);

        // 1. 현재 세션 ID 추출
        final String currentSessionId = getCurrentSessionId(servletRequest);

        // 2. 중복 세션 처리
        checkDuplicateSession(authentication, loginRequest, currentSessionId);

        // 3. 세션 등록/갱신 처리
        updateSession(servletRequest, authentication, currentSessionId, loginId);

        return new CommonResponse<>(resultCode);
    }

    private IResultCode getLoginResultCode(final String loginId) {
        // 계정에 대한 유효성은 AuthenticationManager 에서 처리했으므로 orElse 는 의미 없음
        final Account account = accountRepository.findByLoginId(loginId).orElse(Account.builder().build());

        // 로그인이 성공한 상황이므로 쌓여있던 fail count 는 초기화 후 ResultCode 판단해서 반환
        account.initFailCount();
        return account.isFirstLogin() ? IdentityResultCode.NEED_CHANGE_PASSWORD : CommonResultCode.SUCCESS;
    }

    private String getCurrentSessionId(final HttpServletRequest servletRequest) {
        // 요청 정보로 부터 sessionId 추출 -> session 정보가 없으면 null 반환
        final HttpSession currentSession = servletRequest.getSession(false);
        return currentSession != null ? currentSession.getId() : null;
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

    private void checkDuplicateSession(final Authentication authentication, final PostLoginRequest loginRequest,
                                       final String currentSessionId) {
        final List<SessionInformation> relatedSessions = getRelatedSessions(authentication, currentSessionId);

        // 다른 세션이 존재하면 force 옵션에 따른 처리
        if (!relatedSessions.isEmpty()) {
            log.debug("related session size : {}", relatedSessions.size());
            if (Boolean.TRUE.equals(loginRequest.force())) {
                // force : true -> 기존 세션 전부 삭제 ( 현재 구조에서는 세션은 1개만 존재해야 함 )
                relatedSessions.forEach(SessionInformation::expireNow);
                log.info("force login: expired other session for [{}]", loginRequest.loginId());
            } else {
                // force : false -> 로그인 실패 ( exception 처리 )
                log.error("login denied: other session found for [{}]", loginRequest.loginId());
                throw new CommonRuntimeException(IdentityResultCode.ALREADY_LOGIN);
            }
        }
    }

    private void updateSession(final HttpServletRequest servletRequest, final Authentication authentication,
                               final String currentSessionId, final String loginId) {
        // authentication 을 securityContext 로 wrapping 해서 session attribute 에 추가
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        final HttpSession httpSession = servletRequest.getSession(true);    // 세션이 없으면 신규 생성
        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // SessionRegistry 등록/갱신
        if (currentSessionId == null || !currentSessionId.equals(httpSession.getId())) {
            registerNewSession(authentication, httpSession, loginId);
        } else {
            refreshSessions(httpSession, currentSessionId, loginId);
        }
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

    void handleFailedLogin(final String loginId) {
        accountRepository.findByLoginId(loginId).ifPresent(this::changeStatusByFailedLogin);
    }

    private void changeStatusByFailedLogin(final Account account) {
        final long currentFailCount = account.getFailCount() + 1;
        if (currentFailCount >= 6) {
            // 로그인 실패 횟수가 임계치를 넘겼을 경우 계정 잠금
            final ZonedDateTime lockedExpiredTime = DateTimeUtil.nowUtcPlusMinutes(30L);
            log.error("account will be locked: lockout expire time ~ {}", lockedExpiredTime);
            account.lockAccount(lockedExpiredTime);
        } else {
            // 로그인 실패 횟수 증가
            log.debug("increase fail count: {}", currentFailCount);
            account.increaseFailCount();
        }
    }
}

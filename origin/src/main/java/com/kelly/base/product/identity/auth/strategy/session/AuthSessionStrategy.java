package com.kelly.base.product.identity.auth.strategy.session;

import com.kelly.base.product.identity.auth.dto.PostLoginRequest;
import com.kelly.base.product.identity.auth.strategy.AuthenticationStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 세션 기반 로그인 전략 구현체<p>
 * ConditionalOnProperty 값 : "session"
 *
 * @author 서강희
 */
@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
        name = "config.options.auth-strategy",  // bean 생성 여부를 결정할 property
        havingValue = "session",                // 해당 property 값이 'session' 이면 생성
        matchIfMissing = true                   // property 를 따로 선언해주지 않을 경우 기본으로 생성
)
public class AuthSessionStrategy implements AuthenticationStrategy {
    private final AuthSessionManager authSessionManager;

    /**
     * 로그인 처리 ( session 기반 )
     *
     * @param authentication 인증 정보
     * @param loginRequest   로그인을 위한 정보
     * @param servletRequest HTTP 요청 정보
     */
    @Override
    public void handleLogin(@NonNull Authentication authentication, @NonNull PostLoginRequest loginRequest,
                            @NonNull HttpServletRequest servletRequest) {
        final boolean isForce = Boolean.TRUE.equals(loginRequest.force());
        authSessionManager.dispatchSession(authentication, servletRequest, isForce);
    }

    /**
     * 로그아웃 처리 ( session 기반 )
     *
     * @param servletRequest HTTP 요청 정보
     */
    @Override
    public void handleLogout(@NonNull HttpServletRequest servletRequest) {
        authSessionManager.invalidateSession(servletRequest);
    }
}

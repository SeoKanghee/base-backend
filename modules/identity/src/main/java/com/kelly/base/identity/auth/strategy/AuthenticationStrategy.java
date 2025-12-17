package com.kelly.base.identity.auth.strategy;

import com.kelly.base.identity.auth.dto.PostLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;

/**
 * 로그인 전략을 구현하기 위한 interface
 *
 * @author 서강희
 */
public interface AuthenticationStrategy {
    /**
     * 로그인 처리
     *
     * @param authentication 인증 정보
     * @param loginRequest   로그인을 위한 정보
     * @param servletRequest HTTP 요청 정보
     */
    void handleLogin(@NonNull Authentication authentication, @NonNull PostLoginRequest loginRequest,
                     @NonNull HttpServletRequest servletRequest);

    /**
     * 로그아웃 처리
     *
     * @param servletRequest HTTP 요청 정보
     */
    void handleLogout(@NonNull HttpServletRequest servletRequest);
}

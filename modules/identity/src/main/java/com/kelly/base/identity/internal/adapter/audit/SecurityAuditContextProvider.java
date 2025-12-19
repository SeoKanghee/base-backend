package com.kelly.base.identity.internal.adapter.audit;

import com.kelly.base.common.interfaces.IAuditContextProvider;
import com.kelly.base.identity.internal.adapter.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Spring Security 기반의 AuditContextProvider 구현체
 * <p>
 * SecurityContext에서 현재 로그인한 사용자 정보를 추출하여 audit log에 제공합니다.
 *
 * @author 서강희
 */
@Component
public class SecurityAuditContextProvider implements IAuditContextProvider {
    @Override
    public String getDetailedInfo() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return String.format("userId: %s, accountId: %d, role: %s",
                                 userDetails.getUsername(), userDetails.getAccountId(), userDetails.getRoleCode());
        }

        return null;    // 로그인하지 않은 사용자 또는 인증 정보가 없는 경우
    }
}

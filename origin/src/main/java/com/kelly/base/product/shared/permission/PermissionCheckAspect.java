package com.kelly.base.product.shared.permission;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.product.shared.permission.annotation.RequirePermission;
import com.kelly.base.product.shared.response.identity.IdentityResultCode;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kelly.base.common.CommonConstants.OrderInfo.ASPECT_ORDER_PERMISSION_CHECK;

/**
 * RequirePermission 어노테이션을 처리하는 Aspect
 *
 * @author 서강희
 */
@Generated  // 임시
@Slf4j
@Aspect
@Component
@Order(ASPECT_ORDER_PERMISSION_CHECK)
public class PermissionCheckAspect {
    /**
     * RequirePermission 어노테이션이 적용된 메서드 실행 전에 권한 체크
     *
     * @param requirePermission 어노테이션
     * @throws CommonRuntimeException 권한이 없는 경우
     */
    @Before("@annotation(requirePermission)")
    public void checkPermission(final RequirePermission requirePermission) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkAuthentication(authentication);    // 인증 정보 확인

        // 사용자의 권한 목록 추출
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        final Set<String> userAuthorities = authorities.stream()
                                                       .map(GrantedAuthority::getAuthority)
                                                       .collect(Collectors.toSet());
        log.debug("user authorities: {}", userAuthorities);

        // 필요한 권한 목록
        final String[] requiredPermissions = requirePermission.value();
        final PermOperator operator = requirePermission.operator();

        // 권한 체크
        final boolean hasPermission = checkPermissions(userAuthorities, requiredPermissions, operator);

        if (!hasPermission) {
            final String detailedMessage = String.format(
                    "access denied. required permissions (%s): %s",
                    operator, Arrays.toString(requiredPermissions)
            );
            log.error("permission check failed for user '{}': {}", authentication.getName(), detailedMessage);
            throw new CommonRuntimeException(IdentityResultCode.NO_PERMISSION, detailedMessage);
        }

        log.debug("permission check passed for user '{}'", authentication.getName());
    }

    /**
     * 인증 정보 확인<p>
     * SecurityConfig 가 정상적으로 동작했다면 이 단계에서 인증 정보가 없을 수 없으나,
     * permitAll 예외 URL 누락 실수를 여기서 처리
     *
     * @param authentication 인증 정보
     * @throws CommonRuntimeException 인증 정보가 없는 경우
     */
    void checkAuthentication(final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("authentication credentials are missing or invalid");
            throw new CommonRuntimeException(IdentityResultCode.AUTH_REQUIRED);
        }
    }

    /**
     * 권한 체크 로직
     *
     * @param userAuthorities     사용자가 가진 권한들
     * @param requiredPermissions 필요한 권한들
     * @param operator            논리 연산자 (AND/OR)
     * @return 권한이 있으면 true, 없으면 false
     */
    boolean checkPermissions(
            final Set<String> userAuthorities, final String[] requiredPermissions, final PermOperator operator
    ) {
        if (operator == PermOperator.AND) {
            // AND: 모든 권한을 가지고 있어야 함
            return Arrays.stream(requiredPermissions).allMatch(userAuthorities::contains);
        } else {
            // OR: 하나 이상의 권한을 가지고 있으면 됨
            return Arrays.stream(requiredPermissions).anyMatch(userAuthorities::contains);
        }
    }
}

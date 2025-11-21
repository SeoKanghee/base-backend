package com.kelly.base.product.shared.permission.annotation;

import com.kelly.base.product.shared.permission.PermOperator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permission 체크를 위한 커스텀 어노테이션<p>
 * 메서드에 이 어노테이션을 적용하면 해당 메서드 실행 전에 사용자가 지정된 권한을 가지고 있는지 체크합니다.<p>
 * 오타 방지를 위해 <code>Constants.PermissionCode</code> 사용을 권장하나 <code>String</code> 을 직접 작성해도 무방합니다.<p>
 * 예시:
 * <pre>
 * {@code
 * @RequirePermission(Constants.PermissionCode.VIEW_ACCOUNT_LIST)
 * public List<Account> getAccounts() { ... }
 *
 * @RequirePermission(value = {Constants.PermissionCode.MANAGE_ACCOUNT, "VIEW_ACCOUNT_LIST"}, operator = PermOperator.OR)
 * public void deleteAccount() { ... }
 * }
 * </pre>
 *
 * @author kelly
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * 필요한 권한 코드 배열
     *
     * @return permission codes
     */
    String[] value();

    /**
     * 여러 권한이 지정된 경우 논리 연산자 (AND/OR)<p>
     * 기본값은 OR (하나만 있어도 통과)
     *
     * @return 논리 연산자 <code>PermOperator.OR</code>(기본값) 혹은 <code>PermOperator.AND</code>
     */
    PermOperator operator() default PermOperator.OR;
}

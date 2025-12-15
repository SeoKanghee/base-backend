package com.kelly.base.product.identity.adapter.security;

import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import com.kelly.base.product.identity.domain.permission.Permission;
import com.kelly.base.product.identity.domain.role.Role;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 처리를 위한 계정 정보를 담을 객체
 *
 * @author 서강희
 */
@Getter
@ToString
public class CustomUserDetails extends User {
    private final long accountId;   // accountId ( Entity )
    private final String roleCode;  // role code
    private final Set<String> permissionCodes;  // permission codes
    private final String languageCode;  // language code for i18n

    /**
     * 생성자
     *
     * @param account     <code>Account</code> entity
     * @param role        <code>Role</code> entity
     * @param permissions <code>Permission</code> entities
     */
    public CustomUserDetails(final Account account, final Role role, final Set<Permission> permissions) {
        // accountNonExpired, credentialsNonExpired 는 사용하지 않음 -> true
        super(
                account.getLoginId(),
                account.getPassword(),
                account.getStatus() == AccountStatus.ACTIVE,
                true,
                true,
                account.getStatus() != AccountStatus.LOCKED,
                buildAuthorities(role, permissions)
        );

        this.accountId = account.getId();
        this.roleCode = role.getCode();
        this.permissionCodes = permissions.stream().map(Permission::getCode).collect(Collectors.toSet());
        this.languageCode = account.getLanguageCode();
    }

    /**
     * Role과 Permissions를 Spring Security authorities로 변환
     *
     * @param role        Role entity
     * @param permissions Permission entities
     * @return GrantedAuthority 목록
     */
    private static List<GrantedAuthority> buildAuthorities(final Role role, final Set<Permission> permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 1. Role code를 authority로 추가 (예: "ROLE_SITE_MANAGER")
        authorities.add(new SimpleGrantedAuthority(role.getCode()));

        // 2. Permission codes를 authorities로 추가 (예: "MANAGE_MY_ACCOUNT", "MANAGE_ACCOUNT")
        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getCode()))
        );

        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

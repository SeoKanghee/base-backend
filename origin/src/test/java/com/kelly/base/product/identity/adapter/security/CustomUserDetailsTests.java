package com.kelly.base.product.identity.adapter.security;

import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import com.kelly.base.product.identity.domain.permission.Permission;
import com.kelly.base.product.identity.domain.role.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.kelly.base.common.CommonConstants.PermissionCode.MANAGE_MY_ACCOUNT;
import static com.kelly.base.common.CommonConstants.RoleCode.ROLE_GENERAL_USER;

@DisplayName("CustomUserDetailsTests")
class CustomUserDetailsTests {
    @Nested
    @DisplayName("EqualsAndHashCodeTests")
    class EqualsAndHashCodeTests {
        @Test
        @DisplayName("equals and hashCode test")
        void equalsTest() {
            // given
            final CustomUserDetails customUserDetails1 = new CustomUserDetails(
                    Account.builder().id(999L).loginId("test").password("test").name("test")
                           .role(ROLE_GENERAL_USER).status(AccountStatus.ACTIVE)
                           .failCount(0L).isTemp(true).passwordExpiredAt(DateTimeUtil.nowUtc()).build(),
                    Role.builder().code(ROLE_GENERAL_USER).build(),
                    Set.of(Permission.builder().code(MANAGE_MY_ACCOUNT).build())
            );

            final CustomUserDetails customUserDetails2 = new CustomUserDetails(
                    Account.builder().id(999L).loginId("test").password("test").name("test")
                           .role(ROLE_GENERAL_USER).status(AccountStatus.ACTIVE)
                           .failCount(0L).isTemp(true).passwordExpiredAt(DateTimeUtil.nowUtc()).build(),
                    Role.builder().code(ROLE_GENERAL_USER).build(),
                    Set.of(Permission.builder().code(MANAGE_MY_ACCOUNT).build())
            );

            // when, then
            Assertions.assertEquals(customUserDetails1, customUserDetails2);
            Assertions.assertEquals(customUserDetails1.hashCode(), customUserDetails2.hashCode());
        }
    }
}

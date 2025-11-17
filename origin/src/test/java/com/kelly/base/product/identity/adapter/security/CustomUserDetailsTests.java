package com.kelly.base.product.identity.adapter.security;

import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountRole;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
                           .role(AccountRole.GENERAL_USER).status(AccountStatus.ACTIVE)
                           .failCount(0L).isTemp(true).passwordExpiredAt(DateTimeUtil.nowUtc()).build()
            );

            final CustomUserDetails customUserDetails2 = new CustomUserDetails(
                    Account.builder().id(999L).loginId("test").password("test").name("test")
                           .role(AccountRole.GENERAL_USER).status(AccountStatus.ACTIVE)
                           .failCount(0L).isTemp(true).passwordExpiredAt(DateTimeUtil.nowUtc()).build()
            );

            // when, then
            Assertions.assertEquals(customUserDetails1, customUserDetails2);
            Assertions.assertEquals(customUserDetails1.hashCode(), customUserDetails2.hashCode());
        }
    }
}

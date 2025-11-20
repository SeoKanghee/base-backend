package com.kelly.base.product.identity.repository;

import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import com.kelly.base.product.shared.config.QuerydslConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ QuerydslConfig.class })
@DisplayName("AccountRepositoryTests")
public class AccountRepositoryTests {
    @Autowired
    private AccountRepository accountRepository;

    @Nested
    @DisplayName("AccountEntityTests")
    class AccountEntityTests {
        @Test
        @DisplayName("updateProfile test")
        void updateProfileTest() {
            // given
            final Account account = accountRepository.findById(1L).orElseThrow();
            account.updateProfile("tester", "test team", "update profile");

            // when
            final Account stored = Assertions.assertDoesNotThrow(
                    () -> accountRepository.save(account)
            );

            // then
            Assertions.assertEquals(account.getName(), stored.getName());
            Assertions.assertEquals(account.getDepartment(), stored.getDepartment());
            Assertions.assertEquals(account.getMemo(), stored.getMemo());
        }

        @Test
        @DisplayName("changePassword test")
        void changePasswordTest() {
            // given
            final Account account = accountRepository.findById(1L).orElseThrow();
            account.changePassword("new_password", DateTimeUtil.nowUtc());

            // when
            final Account stored = Assertions.assertDoesNotThrow(
                    () -> accountRepository.save(account)
            );

            // then
            Assertions.assertEquals(account.getPassword(), stored.getPassword());
        }

        @Test
        @DisplayName("updateStatus test")
        void updateStatusTest() {
            // given
            final Account account = accountRepository.findById(1L).orElseThrow();
            account.updateStatus(AccountStatus.LOCKED);

            // when
            final Account stored = Assertions.assertDoesNotThrow(
                    () -> accountRepository.save(account)
            );

            // then
            Assertions.assertEquals(account.getStatus(), stored.getStatus());
        }

        @ParameterizedTest
        @CsvSource({
                "ACTIVE, true",
                "INACTIVE, false",
                "LOCKED, false",
                "DELETED, false",
        })
        @DisplayName("isActive test")
        void isActiveTest(final AccountStatus newAccountStatus, final boolean expectedResult) {
            // given
            final Account account = accountRepository.findById(1L).orElseThrow();
            account.updateStatus(newAccountStatus);

            // when
            final Account stored = Assertions.assertDoesNotThrow(
                    () -> accountRepository.save(account)
            );

            // then
            Assertions.assertEquals(expectedResult, stored.isActive());
        }
    }
}

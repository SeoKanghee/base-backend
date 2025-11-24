package com.kelly.base.product.identity.accounts;

import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.product.identity.accounts.dto.GetRetrieveResponse;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.repository.AccountRepository;
import com.kelly.base.product.shared.config.QuerydslConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataJpaTest
@Import({ AccountsService.class, QuerydslConfig.class })
class AccountsServiceTests {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountsService accountsService;

    @Nested
    @DisplayName("RetrieveTests")
    class RetrieveTests {
        @Test
        @DisplayName("retrieve test")
        void retrieveTest() {
            // given
            final List<Account> storedAccounts = accountRepository.findAllByIsHiddenFalse();
            final int expectedSize = storedAccounts.size();

            // when
            final CommonResponse<GetRetrieveResponse> response = Assertions.assertDoesNotThrow(
                    () -> accountsService.retrieve()
            );

            // then
            final GetRetrieveResponse result = response.getResult();
            Assertions.assertNotNull(result);
            Assertions.assertEquals(expectedSize, result.accounts().size());
        }
    }
}

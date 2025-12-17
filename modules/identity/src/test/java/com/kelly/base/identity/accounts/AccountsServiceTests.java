package com.kelly.base.identity.accounts;

import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.PagedResult;
import com.kelly.base.identity.internal.domain.repository.AccountRepository;
import com.kelly.base.identity.accounts.dto.AccountDetailed;
import com.kelly.base.identity.internal.domain.Account;
import com.kelly.base.common.config.QuerydslConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
        @ParameterizedTest
        @CsvSource({
                "1, 3, disable",  // 3개씩 잘라서 2번째 페이지의 첫번째 계정의 loginId -> disable
                "2, 2, invalid",  // 2개씩 잘라서 3번째 페이지의 첫번째 계정의 loginId -> invalid
        })
        @DisplayName("retrieve test - pageable 처리가 가능한 경우")
        void retrieveTest(final int pageNumber, final int pageSize, final String expectedLoginId) {
            // given - 현재 저장된 전체 계정 개수를 확인하기 위한 findAll
            final List<Account> storedAccounts = accountRepository.findAllByIsHiddenFalse();
            final int expectedSize = storedAccounts.size();
            final Pageable pageable
                    = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());

            // when
            final CommonResponse<PagedResult<AccountDetailed>> response = Assertions.assertDoesNotThrow(
                    () -> accountsService.retrieve(pageable)
            );

            // then
            final PagedResult<AccountDetailed> result = response.getResult();
            Assertions.assertNotNull(result);
            Assertions.assertEquals(expectedSize, result.totalElements());
            Assertions.assertEquals(expectedLoginId, result.content().get(0).loginId());
        }

        @Test
        @DisplayName("retrieve test - pageable 에 sort 정보가 없는 경우")
        void retrieveNoSortPageableTest() {
            // given - sort 정보가 없는 상태로 생성
            final Pageable pageable = PageRequest.of(0, 10);

            // when
            final CommonResponse<PagedResult<AccountDetailed>> response = Assertions.assertDoesNotThrow(
                    () -> accountsService.retrieve(pageable)
            );

            // then - sort 정보가 없을 경우 id 로 정렬되므로 첫번째 값이 동일
            final Account storedFirst = accountRepository.findAllByIsHiddenFalse().get(0);
            final AccountDetailed resultFirst = response.getResult().content().get(0);
            Assertions.assertEquals(storedFirst.getId(), resultFirst.id());
        }

        @Test
        @DisplayName("retrieve test - pageable 에 sort key 정보가 잘못된 경우")
        void retrieveWrongSortPageableTest() {
            // given - 잘못된 sort key 정보로 생성
            final Pageable pageable = PageRequest.of(0, 10, Sort.by("wrong"));

            // when
            final CommonResponse<PagedResult<AccountDetailed>> response = Assertions.assertDoesNotThrow(
                    () -> accountsService.retrieve(pageable)
            );

            // then - sort key 를 찾을 수 없을 경우 default 값인 id 로 정렬되므로 첫번째 값이 동일
            final Account storedFirst = accountRepository.findAllByIsHiddenFalse().get(0);
            final AccountDetailed resultFirst = response.getResult().content().get(0);
            Assertions.assertEquals(storedFirst.getId(), resultFirst.id());
        }
    }
}

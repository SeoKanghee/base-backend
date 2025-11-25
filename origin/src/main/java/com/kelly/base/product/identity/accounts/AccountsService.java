package com.kelly.base.product.identity.accounts;

import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.common.response.PagedResult;
import com.kelly.base.product.identity.accounts.dto.AccountDetailed;
import com.kelly.base.product.identity.accounts.mapper.AccountsMapper;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * accounts service
 *
 * @author 서강희
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountsService {
    private final AccountRepository accountRepository;

    /**
     * 계정 전체 목록 조회
     *
     * @param pageable pagination 정보
     * @return HTTP response <code>CommonResultCode</code>
     */
    @Transactional
    public CommonResponse<PagedResult<AccountDetailed>> retrieve(@NonNull Pageable pageable) {
        final Pageable redefinedPageable = redefineRetrievePageable(pageable);  // pageable 재정의
        final Page<Account> accountsPage = accountRepository.findAllByIsHiddenFalse(redefinedPageable);
        return new CommonResponse<>(CommonResultCode.SUCCESS, AccountsMapper.getRetrieveResponses(accountsPage));
    }

    Pageable redefineRetrievePageable(@NonNull final Pageable pageable) {
        final List<String> allowableKeys = List.of("id", "loginId", "name", "role", "lastLoginAt");
        final boolean hasInvalidSortKey
                = pageable.getSort().stream().anyMatch(order -> !allowableKeys.contains(order.getProperty()));

        // sort 정보가 없거나, sort 가능한 key 가 없으면 default 값으로 정의해서 사용
        // - 정상적인 호출인 경우 controller 에 PageableDefault 값이 정의되어 있어 invalidSortKey 인 경우만 처리됨
        if (pageable.getSort().isUnsorted() || hasInvalidSortKey) {
            log.debug("use default pagination - org info: {}", pageable.getSort());
            return PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").ascending()
            );
        } else {
            return pageable;
        }
    }
}

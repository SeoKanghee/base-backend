package com.kelly.base.product.identity.accounts.mapper;

import com.kelly.base.common.response.PagedResult;
import com.kelly.base.product.identity.accounts.dto.AccountDetailed;
import com.kelly.base.product.identity.domain.account.Account;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * accounts mapper (utility class)
 *
 * @author 서강희
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountsMapper {
    /**
     * <code>Page</code> 로 정리된 <code>Account</code>(entity) 를 <code>PagedResult</code> 로 정리된 <code>AccountDetailed</code> 변환
     *
     * @param page Pageable Entity
     * @return <code>PagedResult</code>
     */
    public static PagedResult<AccountDetailed> getRetrieveResponses(final Page<Account> page) {
        return new PagedResult<>(
                page.getContent().stream().map(
                        account -> new AccountDetailed(
                                account.getId(), account.getStatus().name(), account.getLoginId(), account.getName(),
                                account.getRole(), account.getDepartment(), account.getMemo(),
                                account.getLastLoginAt(), account.getCreatedAt()
                        )
                ).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}

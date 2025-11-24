package com.kelly.base.product.identity.accounts.mapper;

import com.kelly.base.product.identity.accounts.dto.GetRetrieveResponse;
import com.kelly.base.product.identity.accounts.dto.unit.AccountDetailed;
import com.kelly.base.product.identity.domain.account.Account;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * accounts mapper (utility class)
 *
 * @author 서강희
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountsMapper {
    /**
     * Account(entity) 를 <code>GetRetrieveResponse</code> 로 변환
     *
     * @param rawData account(entity) list
     * @return <code>GetRetrieveResponse</code>
     */
    public static GetRetrieveResponse getRetrieveResponses(final List<Account> rawData) {
        return new GetRetrieveResponse(
                rawData.stream().map(
                        account -> new AccountDetailed(
                                account.getStatus().name(), account.getLoginId(), account.getName(), account.getRole(),
                                account.getDepartment(), account.getMemo(),
                                account.getLastLoginAt(), account.getCreatedAt()
                        )
                ).toList()
        );
    }
}

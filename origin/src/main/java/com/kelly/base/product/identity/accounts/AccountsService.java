package com.kelly.base.product.identity.accounts;

import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.product.identity.accounts.dto.GetRetrieveResponse;
import com.kelly.base.product.identity.accounts.mapper.AccountsMapper;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * @return HTTP response <code>CommonResultCode</code>
     */
    @Transactional
    public CommonResponse<GetRetrieveResponse> retrieve() {
        final List<Account> accounts = accountRepository.findAllByIsHiddenFalse();  // hidden 계정 제외
        return new CommonResponse<>(CommonResultCode.SUCCESS, AccountsMapper.getRetrieveResponses(accounts));
    }
}

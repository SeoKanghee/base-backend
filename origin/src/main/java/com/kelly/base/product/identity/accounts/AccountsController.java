package com.kelly.base.product.identity.accounts;

import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.product.identity.accounts.dto.GetRetrieveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kelly.base.product.shared.Constants.UrlInfo.URI_ROOT_ACCOUNTS;

/**
 * accounts controller
 *
 * @author 서강희
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "API for accounts")
@RestController
@RequestMapping(value = URI_ROOT_ACCOUNTS)
public class AccountsController {
    private final AccountsService accountsService;

    @Operation(summary = "계정 조회")
    @GetMapping(value = "")
    public CommonResponse<GetRetrieveResponse> retrieve() {
        log.info("[get] retrieve");
        return accountsService.retrieve();
    }
}

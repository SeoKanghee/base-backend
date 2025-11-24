package com.kelly.base.product.identity.accounts.dto;

import com.kelly.base.product.identity.accounts.dto.unit.AccountDetailed;

import java.util.List;

/**
 * 계정 목록 조회 - response payload
 *
 * @author 서강희
 */
public record GetRetrieveResponse(
        List<AccountDetailed> accounts
) {
}

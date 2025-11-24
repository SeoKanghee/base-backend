package com.kelly.base.product.identity.accounts.dto.unit;

import java.time.ZonedDateTime;

/**
 * 계정 상세
 *
 * @author 서강희
 */
public record AccountDetailed(
        String status,
        String loginId,
        String name,
        String role,
        String department,
        String memo,
        ZonedDateTime lastLoginAt,
        ZonedDateTime createdAt
) {
}

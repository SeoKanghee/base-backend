package com.kelly.base.product.identity.accounts.dto;

import java.time.ZonedDateTime;

/**
 * 계정 상세
 *
 * @param id          계정 시퀀스 ID
 * @param status      계정 상태
 * @param loginId     로그인 ID
 * @param name        사용자 이름
 * @param role        계정 권한
 * @param department  사용자 추가 정보 #1
 * @param memo        사용자 추가 정보 #2
 * @param lastLoginAt 마지막으로 로그인한 시간
 * @param createdAt   계정이 생성된 시간
 * @author 서강희
 */
public record AccountDetailed(
        long id,
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

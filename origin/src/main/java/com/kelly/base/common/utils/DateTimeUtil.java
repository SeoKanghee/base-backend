package com.kelly.base.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * DateTimeUtil
 *
 * @author 서강희
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {
    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    /**
     * 현재시간을 선택한 ZoneId 에 맞게 반환
     *
     * @param zone <code>java.time.ZoneId</code>
     * @return 현재시간 (ZoneId)
     */
    public static ZonedDateTime getNow(final ZoneId zone) {
        return ZonedDateTime.now(zone);
    }

    /**
     * 현재시간을 UTC 로 반환
     *
     * @return 현재시간 (UTC)
     */
    public static ZonedDateTime nowUtc() {
        return getNow(ZONE_ID_UTC);
    }

    /**
     * 현재시간을 기준으로 정해진 분 (minutes) 이후의 시간 반환 (UTC)
     *
     * @param minutes 계산할 분 (minutes)
     * @return 현재시간 + <code>minutes</code> (UTC)
     */
    public static ZonedDateTime nowUtcPlusMinutes(final long minutes) {
        return nowUtc().plusMinutes(minutes);
    }

    /**
     * 현재시간을 기준으로 정해진 분 (minutes) 이전의 시간 반환 (UTC)
     *
     * @param minutes 계산할 분 (minutes)
     * @return 현재시간 - <code>minutes</code> (UTC)
     */
    public static ZonedDateTime nowUtcMinusMinutes(final long minutes) {
        return nowUtc().minusMinutes(minutes);
    }
}

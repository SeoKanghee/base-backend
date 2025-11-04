package com.kelly.base.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {
    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    public static ZonedDateTime nowUtc() {
        return getNow(ZONE_ID_UTC);
    }

    public static ZonedDateTime getNow(ZoneId zone) {
        return ZonedDateTime.now(zone);
    }
}

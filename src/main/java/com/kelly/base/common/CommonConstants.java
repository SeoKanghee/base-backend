package com.kelly.base.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstants {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OrderInfo {
        // ExceptionHandler 의 order 상수 정의
        public static final int EXCEPTION_HANDLER_ORDER_COMMON = 100;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DateTimePattern {
        // audit log 에서 출력되는 시간에 대한 pattern 정의
        public static final String AUDIT_LOG_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    }
}

package com.kelly.base.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstants {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OrderInfo {
        // ExceptionHandler 의 order 상수 정의
        public static final int EXCEPTION_HANDLER_ORDER_COMMON = 100;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CommonAsync {
        public static final String COMMON_ASYNC_NAME = "commonAsync";   // async 이름
        public static final String COMMON_ASYNC_THREAD_PREFIX = "common-async-";    // thread prefix 값
        public static final int COMMON_ASYNC_CORE_POOL_SIZE = 10;   // 항상 활성 상태로 유지할 thread 개수
        public static final int COMMON_ASYNC_QUEUE_CAPACITY = 500;  // thread 가 full 일 경우 작업들을 쌓아둘 queue 크기
        public static final int COMMON_ASYNC_MAX_POOL_SIZE = 10;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DateTimePattern {
        // audit log 에서 출력되는 시간에 대한 pattern 정의
        public static final String AUDIT_LOG_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AuditConstants {
        // audit log 에서 출력되는 시간에 대한 pattern 정의
        public static final String LOG_PATTERN = DateTimePattern.AUDIT_LOG_PATTERN;

        // audit log 저장을 위해 사용하는 request body 저장용 attribute
        public static final String ATTR_AUDIT_REQ_BODY = "ATTR_AUDIT_REQ_BODY";

        // audit log 저장에 기본적으로 예외 처리되어야 할 uri
        public static final List<String> EXCLUDED_URI_PATTERNS = List.of(
                "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**"
        );
    }
}

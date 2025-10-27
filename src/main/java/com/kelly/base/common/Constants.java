package com.kelly.base.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OrderInfo {
        // ExceptionHandler 의 order 상수 정의
        public static final int EXCEPTION_HANDLER_ORDER_COMMON = 100;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class UrlInfo {
        public static final String URI_PREFIX_API = "/api";
        public static final String WITH_SUB_PATHS = "/**";

        public static final String URI_ROOT_AUTH = URI_PREFIX_API + "/auth";    // API - auth
        public static final String URI_ROOT_USERS = URI_PREFIX_API + "/users";  // API - users
    }
}

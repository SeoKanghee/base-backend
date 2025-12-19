package com.kelly.base.identity.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    /**
     * Identity URL 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class UrlInfo {
        public static final String URI_PREFIX_API = "/api";
        public static final String WITH_SUB_PATHS = "/**";

        public static final String URI_ROOT_AUTH = URI_PREFIX_API + "/auth";    // API - auth
        public static final String URI_ROOT_ACCOUNTS = URI_PREFIX_API + "/accounts";    // API - accounts
    }
}

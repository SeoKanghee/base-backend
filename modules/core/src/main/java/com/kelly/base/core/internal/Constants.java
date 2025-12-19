package com.kelly.base.core.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    /**
     * Core URL 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class UrlInfo {
        public static final String URI_PREFIX_API = "/api";
        public static final String WITH_SUB_PATHS = "/**";

        public static final String URI_ROOT_SYSTEM = URI_PREFIX_API + "/system";    // API - system
    }
}

package com.kelly.base.product.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 제품, 기능 간에 공유되는 상수 선언
 *
 * @author 서강희
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    /**
     * URL 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class UrlInfo {
        public static final String URI_PREFIX_API = "/api";
        public static final String WITH_SUB_PATHS = "/**";

        public static final String URI_ROOT_AUTH = URI_PREFIX_API + "/auth";    // API - auth
        public static final String URI_ROOT_ACCOUNTS = URI_PREFIX_API + "/accounts";    // API - accounts
    }

    /**
     * RoleCode 정의 -> DB schema 에 있는 값과 일치해야 함
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RoleCode {
        public static final String ROLE_SITE_MANAGER = "ROLE_SITE_MANAGER";
        public static final String ROLE_SERVICE_ENGINEER = "ROLE_SERVICE_ENGINEER";
        public static final String ROLE_ADVANCED_USER = "ROLE_ADVANCED_USER";
        public static final String ROLE_GENERAL_USER = "ROLE_GENERAL_USER";
        public static final String ROLE_DEMO_USER = "ROLE_DEMO_USER";
    }

    /**
     * PermissionCode 정의 -> DB schema 에 있는 값과 일치해야 함
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PermissionCode {
        public static final String MANAGE_MY_ACCOUNT = "MANAGE_MY_ACCOUNT";
        public static final String VIEW_ACCOUNT_LIST = "VIEW_ACCOUNT_LIST";
        public static final String MANAGE_ACCOUNT = "MANAGE_ACCOUNT";
    }
}

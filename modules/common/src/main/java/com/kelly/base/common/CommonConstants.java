package com.kelly.base.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstants {

    /**
     * Order 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OrderInfo {
        // Aspect 의 order 상수 정의 ( 1 ~ 1000 )
        public static final int ASPECT_ORDER_PERMISSION_CHECK = 100;

        // ExceptionHandler 의 order 상수 정의 ( 0 ~ 1000 )
        public static final int EXCEPTION_HANDLER_ORDER_COMMON = 999;
    }

    /**
     * 공통 ThreadPoolTaskExecutor 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CommonAsync {
        public static final String COMMON_ASYNC_NAME = "commonAsync";   // async 이름
        public static final String COMMON_ASYNC_THREAD_PREFIX = "common-async-";    // thread prefix 값
        public static final int COMMON_ASYNC_CORE_POOL_SIZE = 10;   // 항상 활성 상태로 유지할 thread 개수
        public static final int COMMON_ASYNC_QUEUE_CAPACITY = 500;  // thread 가 full 일 경우 작업들을 쌓아둘 queue 크기
        public static final int COMMON_ASYNC_MAX_POOL_SIZE = 10;
    }

    /**
     * Date Time Pattern 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DateTimePattern {
        // audit log 에서 출력되는 시간에 대한 pattern 정의
        public static final String AUDIT_LOG_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    }

    /**
     * Audit Log 정의
     */
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

    /**
     * URL 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class UrlInfo {
        public static final String URI_PREFIX_API = "/api";
        public static final String WITH_SUB_PATHS = "/**";

        public static final String URI_ROOT_AUTH = URI_PREFIX_API + "/auth";    // API - auth
        public static final String URI_ROOT_ACCOUNTS = URI_PREFIX_API + "/accounts";    // API - accounts
        public static final String URI_ROOT_SYSTEM = URI_PREFIX_API + "/system";    // API - system
    }

    /**
     * RoleCode 정의<p>
     * - DB schema 에 있는 값과 일치해야 함
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
     * PermissionCode 정의<p>
     * - DB schema 에 있는 값과 일치해야 함
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PermissionCode {
        public static final String MANAGE_MY_ACCOUNT = "MANAGE_MY_ACCOUNT";
        public static final String VIEW_ACCOUNT_LIST = "VIEW_ACCOUNT_LIST";
        public static final String MANAGE_ACCOUNT = "MANAGE_ACCOUNT";
        public static final String MANAGE_SYSTEM = "MANAGE_SYSTEM";
    }

    /**
     * Swagger API 문서화 관련 상수
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SwaggerDescription {
        public static final String ACCOUNTS_RETRIEVE = """
                계정 목록을 pagination 을 적용하여 조회합니다. ( Spring Data Jpa Pageable )
                
                **page(0~n):** 몇 번째 page 를 가져올지 선택합니다.
                
                **size(default 10):** 한 page 에 몇 개의 contents 를 담을지 선택합니다.
                
                **sort: 정렬 가능한 필드 정보는 아래와 같습니다.**
                - id: 계정 sequence ID
                - loginId: 사용자 ID
                - name: 사용자 이름
                - role: 사용자 권한
                - lastLoginAt: 마지막 로그인 일시
                
                **사용 예시:**
                - sort=id,asc (기본값)
                - sort=lastLoginAt,desc
                - sort=loginId,asc&sort=lastLoginAt,desc (다중 정렬)
                """;
    }
}

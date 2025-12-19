# Swagger 구현 가이드

이 문서는 모듈별 Swagger 설정 방법을 설명합니다.

---

## 1. 개요

본 프로젝트는 [SpringDoc OpenAPI](https://springdoc.org/)를 사용하여 API 문서를 자동 생성합니다. Swagger 설정은 크게 두 가지로 나뉩니다:

1. **공통 설정 (CommonSwaggerConfig)**: 기본 OpenAPI 정보 설정
2. **모듈별 설정 (e.g., IdentitySwaggerConfig)**: API 그룹화 설정

---

## 2. 공통 Swagger 설정

### 2.1 CommonSwaggerConfig

공통 모듈에서 기본 OpenAPI Bean을 정의합니다.

**파일 위치:** `modules/common/src/main/java/com/kelly/base/common/config/CommonSwaggerConfig.java`

```java
package com.kelly.base.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommonSwaggerConfig {
    private final PropertiesConfig propertiesConfig;

    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI openAPI() {
        Info info = new Info().title("API definition - " + propertiesConfig.getApplicationName())
                              .version(propertiesConfig.getApplicationVersion())
                              .description("for Test");
        return new OpenAPI().info(info);
    }
}
```

### 2.2 주요 특징

| 항목 | 설명 |
|------|------|
| `@ConditionalOnMissingBean` | 다른 모듈에서 OpenAPI Bean을 정의하지 않은 경우에만 이 Bean을 생성 |
| `PropertiesConfig` | 애플리케이션 이름과 버전을 가져오기 위한 설정 클래스 |
| `Info` | API 문서의 제목, 버전, 설명 등 메타 정보 설정 |

---

## 3. 모듈별 Swagger 설정

### 3.1 GroupedOpenApi를 사용한 API 그룹화

각 모듈에서는 `GroupedOpenApi`를 사용하여 API를 그룹별로 분류합니다.

**예시 파일:** `modules/identity/src/main/java/com/kelly/base/identity/internal/config/IdentitySwaggerConfig.java`

```java
package com.kelly.base.identity.internal.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kelly.base.identity.internal.Constants.UrlInfo.*;

@Configuration
public class IdentitySwaggerConfig {
    @Bean
    public GroupedOpenApi authOpenApi() {
        String[] paths = { URI_ROOT_AUTH + WITH_SUB_PATHS };
        return GroupedOpenApi.builder().group("auth").pathsToMatch(paths).build();
    }

    @Bean
    public GroupedOpenApi accountsOpenApi() {
        String[] paths = { URI_ROOT_ACCOUNTS + WITH_SUB_PATHS };
        return GroupedOpenApi.builder().group("accounts").pathsToMatch(paths).build();
    }
}
```

### 3.2 모듈별 URL 상수 정의

각 모듈은 자체 `internal/Constants.java` 파일에 URL 상수를 정의합니다.

**Identity 모듈 예시:** `modules/identity/src/main/java/com/kelly/base/identity/internal/Constants.java`

```java
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

        public static final String URI_ROOT_AUTH = URI_PREFIX_API + "/auth";        // /api/auth
        public static final String URI_ROOT_ACCOUNTS = URI_PREFIX_API + "/accounts"; // /api/accounts
    }
}
```

**Core 모듈 예시:** `modules/core/src/main/java/com/kelly/base/core/internal/Constants.java`

```java
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

        public static final String URI_ROOT_SYSTEM = URI_PREFIX_API + "/system";    // /api/system
    }
}
```

### 3.3 GroupedOpenApi 구성 요소

| 메서드 | 설명 | 예시 |
|--------|------|------|
| `group()` | API 그룹 이름 (Swagger UI 드롭다운에 표시) | `"auth"`, `"accounts"` |
| `pathsToMatch()` | 포함할 API 경로 패턴 | `{ "/api/auth/**" }` |
| `pathsToExclude()` | 제외할 API 경로 패턴 | `{ "/api/internal/**" }` |
| `packagesToScan()` | 스캔할 패키지 지정 | `"com.kelly.base.identity"` |

---

## 4. 새로운 모듈 Swagger 설정 추가 방법

### 4.1 단계별 가이드

#### Step 1: 모듈 내 Constants 클래스 생성

새 모듈의 `internal` 패키지에 Constants 클래스를 생성하고 URL 상수를 정의합니다:

```java
package com.kelly.base.{모듈명}.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    /**
     * {모듈명} URL 정의
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class UrlInfo {
        public static final String URI_PREFIX_API = "/api";
        public static final String WITH_SUB_PATHS = "/**";

        public static final String URI_ROOT_PRODUCTS = URI_PREFIX_API + "/products";  // /api/products
    }
}
```

#### Step 2: 모듈 내 SwaggerConfig 클래스 생성

새 모듈의 `internal/config` 패키지에 SwaggerConfig 클래스를 생성합니다:

```java
package com.kelly.base.{모듈명}.internal.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kelly.base.{모듈명}.internal.Constants.UrlInfo.*;

@Configuration
public class {모듈명}SwaggerConfig {
    
    @Bean
    public GroupedOpenApi {그룹명}OpenApi() {
        String[] paths = { URI_ROOT_{모듈} + WITH_SUB_PATHS };
        return GroupedOpenApi.builder()
                .group("{그룹명}")
                .pathsToMatch(paths)
                .build();
    }
}
```

#### Step 3: 여러 그룹이 필요한 경우

하나의 모듈에서 여러 API 그룹을 제공해야 하는 경우, 각 그룹별로 Bean을 정의합니다:

```java
@Configuration
public class ProductSwaggerConfig {
    
    @Bean
    public GroupedOpenApi productOpenApi() {
        String[] paths = { URI_ROOT_PRODUCTS + WITH_SUB_PATHS };
        return GroupedOpenApi.builder()
                .group("products")
                .pathsToMatch(paths)
                .build();
    }
    
    @Bean
    public GroupedOpenApi categoryOpenApi() {
        String[] paths = { URI_ROOT_CATEGORIES + WITH_SUB_PATHS };
        return GroupedOpenApi.builder()
                .group("categories")
                .pathsToMatch(paths)
                .build();
    }
}
```

### 4.2 네이밍 컨벤션

| 항목 | 컨벤션 | 예시 |
|------|--------|------|
| 설정 클래스명 | `{모듈명}SwaggerConfig` | `IdentitySwaggerConfig`, `ProductSwaggerConfig` |
| Bean 메서드명 | `{그룹명}OpenApi` | `authOpenApi()`, `accountsOpenApi()` |
| 그룹명 | 소문자 kebab-case 또는 camelCase | `"auth"`, `"accounts"`, `"user-management"` |
| 상수 클래스명 | `Constants` | 모든 모듈에서 동일하게 사용 |

### 4.3 파일 위치 구조

```
modules/{모듈명}/
└── src/main/java/com/kelly/base/{모듈명}/
    └── internal/
        ├── Constants.java              # URL 상수 정의
        └── config/
            └── {모듈명}SwaggerConfig.java  # Swagger 그룹 설정
```

---

## 5. API 설명 상수 활용

- API 문서에 반복적으로 사용되는 설명은 `CommonConstants.SwaggerDescription`에 상수로 정의할 예정입니다.
- `common`모듈에 대한 수정이므로 협의를 통해 추가됩니다.

```java
public static final class SwaggerDescription {
    public static final String ACCOUNTS_RETRIEVE = """
            계정 목록을 pagination 을 적용하여 조회합니다. ( Spring Data Jpa Pageable )
            
            **page(0~n):** 몇 번째 page 를 가져올지 선택합니다.
            ...
            """;
}
```

Controller에서 사용 예시:

```java
@Operation(summary = "계정 목록 조회", description = SwaggerDescription.ACCOUNTS_RETRIEVE)
@GetMapping
public ResponseEntity<Page<AccountResponse>> getAccounts(Pageable pageable) {
    // ...
}
```

---

## 6. Swagger UI 접근

애플리케이션 실행 후 아래 URL로 Swagger UI에 접근할 수 있습니다:

- **Swagger UI**
    - `http://localhost:{port}/api.html`
    - `http://localhost:{port}/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:{port}/v3/api-docs`

## 7. 참고 자료

- [SpringDoc 공식 문서](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)

---

## 작성자

서강희

## 변경 이력

| 버전 | 날짜 | 변경 내역 |
|-----|------|---------|
| 1.0 | 2025-12-19 | 멀티 모듈 구조 초안 작성 |

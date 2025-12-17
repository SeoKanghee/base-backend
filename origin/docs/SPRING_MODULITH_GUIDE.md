# Spring Modulith 구현 가이드

- 기능 개발 담당자들이 각자 개발해야하는 기능에 대해 논리적인 모듈을 구축하고 관리할 수 있도록 하기 위해 적용합니다.
- 모듈간 의존성을 이벤트 기반으로 분리해서 순환 참조 문제를 개선합니다.
- 현재 개발되어 있는 MAGA 기반으로 적용합니다.
    - Common 기능 : audit, i18n, sse
    - Identity 기능 : login, logout

---

## 1. 의존성 추가

### Spring Initializr dependency 확인

- [Spring Initializr](https://start.spring.io/) 기준 spring boot 4.0.0 과 매칭되는 Spring Modulith 는 2.0.0 입니다.

```kotlin
extra["springModulithVersion"] = "2.0.0"

dependencies {
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}
```

### dependency 적용

- 기존과 동일하게 version catalog 방식을 통해 버전을 일괄 관리합니다.

#### libs.versions.toml

```toml
[versions]
spring-modulith = "2.0.0"

[libraries]
spring-modulith-starter-core = { group = "org.springframework.modulith", name = "spring-modulith-starter-core", version.ref = "spring-modulith" }
spring-modulith-starter-test = { group = "org.springframework.modulith", name = "spring-modulith-starter-test", version.ref = "spring-modulith" }
spring-modulith-docs = { group = "org.springframework.modulith", name = "spring-modulith-docs", version.ref = "spring-modulith" }
```

#### build.gradle.kts

```kotlin
dependencies {
    implementation(libs.spring.modulith.starter.core)

    testImplementation(libs.spring.modulith.starter.test)
    testImplementation(libs.spring.modulith.docs)
}
```

---

## 2. 모듈 구조 정의

### 패키지 구조 재정의

```
com.kelly.base
├── common/                       # Common 모듈
│   ├── api/                      # 다른 모듈에서 접근 가능한 API
│   ├── exception/
│   ├── response/
│   └── utils/
│
├── identity/                     # Identity 모듈
│   ├── api/                      # 다른 모듈에서 접근 가능한 API
│   │   └── dto/
│   ├── internal/
│   │   ├── domain/
│   │   ├── repository/
│   │   ├── service/
│   │   └── adapter/
│   ├── events/                   # 모듈간 통신용 event
│   │   └── SomeEvent.java
│   └── package-info.java         # 모듈 메타데이터
│
└── core/                         # Core 모듈
    ├── api/
    ├── internal/
    └── package-info.java
```  

### package-info.java 예시 (identity 모듈)

- package-info 파일을 통해 모듈에 대한 정의를 작성합니다.

```java
@ApplicationModule(
        displayName = "Identity Module",
        allowedDependencies = {"common"}
)
package com.kelly.base.identity;
```

---  

## 3. 아키텍처 검증

### ModularityTest.java

```java
class ModularityTest {
    ApplicationModules modules = ApplicationModules.of(BaseBackendApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
```

---

## 4. 모듈간 의존성 허용 정의 ( 예시 )

- 특정 모듈의 비지니스 로직이 아닌 인터페이스를 사용해야 하는 경우 `package-info.java` 에 상세 내용을 정의할 수 있습니다.
- 대표적인 예로 identity 모듈에서 관리되는 permission 이 있습니다.
- 아래는 예시 코드로 실제 구현 과정에서 변경이 있을 수 있습니다.

```
com.kelly.base
├── identity/
│   └── permission/
│       ├── annotation/
│       │   ├── package-info.java
│       │   ├── PermOperator.java       # permission operator
│       │   └── RequirePermission.java  # permission annotation
│       └── PermissionCheckAspect.java
│
└── core/
    └── package-info.java
```

### 4-1. identity.permission.annotation

#### package-info.java

```java
@NamedInterface("perm-annotation")
package com.kelly.base.product.identity.permission.annotation;
```

### 4-2. core

#### package-info.java

```java
@ApplicationModule(
        displayName = "Core Module",
        allowedDependencies = {
                "product.identity::perm-annotation"
        }
)
package com.kelly.base.product.core;
```

---

## 5. 모듈간 이벤트 기반 통신 ( 예시 )

- 모듈간 dependency 제거를 위해 전체적인 상황 전파가 필요한 동작의 경우 event 를 pub/sub 합니다.
- 아래는 예시 코드로 실제 구현 과정에서 변경이 있을 수 있습니다.

### 5-1. 이벤트 정의

#### LoginSuccessEvent.java

```java
public record LoginSuccessEvent(
        String loginId,
        String accountName,
        ZonedDateTime loginAt,
        String ipAddress
) {
    public static LoginSuccessEvent of(
            String loginId, String accountName, String ipAddress
    ) {
        return new LoginSuccessEvent(
                loginId, accountName, ZonedDateTime.now(), ipAddress
        );
    }
}
```

#### AccountCreatedEvent.java

```java
public record AccountCreatedEvent(
        Long accountId,
        String loginId,
        String accountName,
        String role
) {
}  
```

### 5-2. 이벤트 발행

#### AuthService.java

```java
public class AuthService {
    private final ApplicationEventPublisher eventPublisher;

    public CommonResponse<Void> login(
            PostLoginRequest loginRequest,
            HttpServletRequest servletRequest
    ) {
        Account account = accountRepository.findByLoginId(
                loginRequest.loginId()
        ).orElseThrow(
                () -> new CommonRuntimeException(
                        IdentityResultCode.ACCOUNT_NOT_FOUND
                )
        );

        // 로그인 성공 시 이벤트 발행
        eventPublisher.publishEvent(
                LoginSuccessEvent.of(
                        account.getLoginId(),
                        account.getName(),
                        getClientIp(servletRequest)
                )
        );

        return new CommonResponse<>(CommonResultCode.SUCCESS);
    }
}
```

### 5-3. 이벤트 구독

#### AuditEventListener.java

```java
public class AuditEventListener {
    private final AuditLogService auditLogService;

    @ApplicationModuleListener
    void onLoginSuccess(LoginSuccessEvent event) {
        auditLogService.logSuccess(
                "LOGIN", event.loginId(),
                String.format(
                        "User '%s' logged in from %s",
                        event.accountName(), event.ipAddress()
                ),
                null
        );
    }

    @ApplicationModuleListener
    void onAccountCreated(AccountCreatedEvent event) {
        auditLogService.logSuccess(
                "ACCOUNT_CREATE", "SYSTEM",
                String.format(
                        "Account created: %s (ID: %d)",
                        event.loginId(), event.accountId()
                ),
                null
        );
    }
}
```

---

## 6. 참고 자료

- [Spring Modulith 공식 문서](https://spring.io/projects/spring-modulith)
- [Spring Modulith Reference](https://docs.spring.io/spring-modulith/reference/)
- [Event Externalization](https://docs.spring.io/spring-modulith/reference/events.html)
- [Testing](https://docs.spring.io/spring-modulith/reference/testing.html)

---

## 작성자

서강희

## 변경 이력

| 버전  | 날짜         | 변경 내역 |     |
|-----|------------|-------|-----|
| 1.0 | 2025-12-17 | 초안 작성 |     |

# Base Backend

- Make Application Great Again
- Spring Boot 4.0 기반의 백엔드 애플리케이션 템플릿입니다.
- Spring Modulith를 활용한 모듈화된 아키텍처와 재사용 가능한 공통 모듈을 제공합니다.

---

## 📋 목차

- [🚀 주요 기능](#🚀-주요-기능)
- [🛠 기술 스택](#🛠-기술-스택)
- [📦 시작하기](#📦-시작하기)
- [📁 프로젝트 구조](#📁-프로젝트-구조)
- [🔧 주요 기능 상세](#🔧-주요-기능-상세)
- [📚 API 문서](#📚-api-문서)
- [🧪 테스트](#🧪-테스트)
- [⚙️ 환경 설정](#⚙️-환경-설정)
- [📖 문서 가이드](#📖-문서-가이드)
- [🔍 추가 정보](#🔍-추가-정보)

---

## 🚀 주요 기능

### Common 모듈 (공통 기능)
- **감사 로깅 시스템**: API 호출, 시스템 이벤트를 자동으로 추적 및 로깅
- **SSE (Server Sent Events)**: 실시간 서버 푸시 알림 지원
- **비동기 처리**: 설정 가능한 비동기 작업 실행 환경
- **전역 예외 처리**: 통일된 예외 처리 및 응답 포맷
- **보안 기능**: 
  - Jasypt 기반 설정 암호화
  - Native Vault를 통한 민감 정보 관리
  - CommonCryptoService를 통한 데이터 암/복호화
- **다국어 (i18n)**: 다국어 지원 메시지 서비스
- **유틸리티**: JSON 처리, 날짜/시간, NanoID 생성 등

### Product 모듈 (비즈니스 로직)
- **Spring Modulith 기반 모듈화**: 독립적이고 테스트 가능한 모듈 구조
- **Identity 모듈**: 
  - 세션 기반 인증/인가
  - 계정 관리
  - 역할 기반 접근 제어
  - 권한 체크 시스템
- **Core 모듈**:
  - 시스템 설정 기능
  - TBU

---

## 🛠 기술 스택

### 프레임워크 & 언어
- **Java 17**
- **Spring Boot 4.0.0**
- **Spring Modulith 2.0.0**
- **Gradle Kotlin DSL**

### 핵심 라이브러리
- **Spring Boot Starter Web**: RESTful API 개발
- **Spring Boot Starter Security**: 인증 및 보안
- **Spring Boot Starter Data JPA**: 데이터 영속성
- **Spring Boot Starter Validation**: 입력 유효성 검사
- **QueryDSL 5.1.0**, **MariaDB JDBC**: 데이터베이스
- **Lombok**: 반복 코드 감소

### 보안 & 암호화
- **Jasypt 3.0.5**: 설정 암호화
- **Native Vault**: JNI 기반 보안 저장소
- **Spring Security**: 인증/인가 프레임워크

### 문서화 & 테스트
- **SpringDoc OpenAPI 3.0.0**: API 문서 자동 생성
- **JUnit 5 & JaCoCo**: 테스트 및 커버리지
- **Spring Modulith Test**: 모듈 구조 검증

### 유틸리티
- **NanoID 1.0.1**: 고유 식별자 생성

---

## 📦 시작하기

### 사전 요구사항

- Java 17 이상
- Gradle 9.2.1 이상
- MariaDB 10.x 이상

### 데이터베이스 설정

1. MariaDB 데이터베이스 생성 및 초기화:
```bash
mysql -u root -p < origin/db-config/00_created_db.sql
```

2. 데이터베이스 스키마:
   - 데이터베이스명: `base_backend`

### 환경 변수 설정

프로젝트 루트 디렉토리에 `.env` 파일을 생성하세요:

```properties
# -----------------------------------------
# Spring 환경 변수
# -----------------------------------------
SPRING_PROFILES_ACTIVE=local
SERVER_PORT=7479

# -----------------------------------------
# Application 환경 변수
# -----------------------------------------
APPLICATION_NAME=base-backend
APPLICATION_VERSION=0.0.1
APPLICATION_GROUP=com.kelly.base
COMMON_CRYPTO_KEY=your_crypto_key
SSE_EMITTER_TIMEOUT_MS=60000

# -----------------------------------------
# DataBase Settings
# -----------------------------------------
DATABASE_NAME=base_backend
DATABASE_SERVER_PORT=3306
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
JPA_DDL_AUTO=validate

# -----------------------------------------
# Logging
# -----------------------------------------
KELLY_LOG_LEVEL=INFO

# -----------------------------------------
# i18n Settings (Optional)
# -----------------------------------------
DEFAULT_LANGUAGE_CODE=en
DEFAULT_REGULATOR_CODE=mfds
```

> **참고**: 민감한 정보(비밀번호, 암호화 키 등)는 Jasypt를 사용하여 `ENC(암호화된_값)` 형식으로 저장할 수 있습니다.

### 빌드 및 실행

```bash
# origin 디렉토리로 이동
cd origin

# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/base-backend.jar
```

애플리케이션은 기본적으로 `http://localhost:7479`에서 실행됩니다.

---

## 📁 프로젝트 구조

```
origin/
├── config/
│   └── i18n/                           # 다국어 리소스
│       ├── common/                     # 공통 메시지
│       │   └── messages_{lang}.json
│       └── product/                    # 제품별 메시지
│           └── messages_{regulator}_{lang}.properties
├── db-config/
│   └── 00_created_db.sql               # DB 초기화 스크립트
├── docs/                               # 문서 가이드
└── src/main/java/com/kelly/base/
    ├── BaseBackendApplication.java     # 메인 애플리케이션
    ├── common/                         # 공통 모듈
    │   ├── audit/                      # 감사 로깅
    │   ├── config/                     # 설정 클래스
    │   ├── crypto/                     # 암호화 서비스
    │   ├── exception/                  # 예외 처리
    │   ├── i18n/                       # 다국어
    │   ├── interfaces/                 # 인터페이스 정의
    │   ├── jni/                        # JNI Native 라이브러리
    │   ├── response/                   # 공통 응답 포맷
    │   ├── sse/                        # Server-Sent Events
    │   └── utils/                      # 유틸리티
    └── product/                        # 비즈니스 로직 (Spring Modulith)
        ├── core/                       # Core 모듈
        │   └── system/                 # 시스템 설정
        └── identity/                   # Identity 모듈
            ├── accounts/               # 계정 관리
            ├── adapter/                # 어댑터
            ├── auth/                   # 인증
            ├── config/                 # 보안 설정
            ├── domain/                 # 도메인 엔티티
            ├── permission/             # 권한 체크
            ├── repository/             # 리포지토리
            └── response/               # Identity 응답 코드
```

---

## 🔧 주요 기능 상세

### 1. 감사 로깅 시스템

모든 API 호출과 시스템 이벤트를 자동으로 로깅합니다.

```java
// 특정 API에서 감사 로깅 제외
@NoAudit
@GetMapping("/public")
public ResponseEntity<?> publicEndpoint() {
    return ResponseEntity.ok("Public data");
}
```

로그 파일:
- `logs/{APPLICATION_NAME}.log`: 일반 애플리케이션 로그
- `logs/{APPLICATION_NAME}-audit.log`: 감사 로그

> 상세 사용법은 [AUDIT_GUIDE.md](origin/docs/AUDIT_GUIDE.md) 참조

### 2. 권한 체크 시스템

어노테이션 기반의 권한 검증을 지원합니다.

```java
// 특정 권한이 있는 사용자만 접근 가능
@RequirePermission(value = "USER_READ", operator = PermOperator.AND)
@GetMapping("/users")
public ResponseEntity<?> getUsers() {
    return ResponseEntity.ok(userService.getAll());
}

// 여러 권한 중 하나라도 있으면 접근 가능
@RequirePermission(value = {"ADMIN", "MANAGER"}, operator = PermOperator.OR)
@DeleteMapping("/users/{id}")
public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.ok().build();
}
```

> 상세 사용법은 [PERMISSION_USAGE_GUIDE.md](origin/docs/PERMISSION_USAGE_GUIDE.md) 참조

### 3. 다국어 (i18n)

다국어 메시지 지원을 제공합니다.

```java
private final II18nMessageService i18nMessageService;

// 현재 언어로 메시지 조회
String message = i18nMessageService.getMessage("error.not_found");

// 파라미터가 있는 메시지
String message = i18nMessageService.getMessage("welcome.user", new Object[]{"Kelly"});
```

메시지 파일 위치:
- `config/i18n/common/`: 공통 메시지 (JSON 형식)
- `config/i18n/product/`: 제품별 메시지 (Properties 형식)

> 상세 사용법은 [I18N_USAGE_GUIDE.md](origin/docs/I18N_USAGE_GUIDE.md) 참조

### 4. SSE (Server-Sent Events)

실시간 서버 푸시 알림을 지원합니다.

```java
private final SseEmitterManager sseEmitterManager;

// 사용자에게 이벤트 전송
SseEvent event = SseEvent.builder()
    .eventType(SseEventType.NOTIFICATION)
    .data(notificationData)
    .build();
sseEmitterManager.sendToUser(userId, event);
```

### 5. 공통 응답 포맷

일관된 API 응답 구조를 제공합니다.

```json
{
  "success": true,
  "code": "S0000",
  "message": "Success",
  "data": { ... }
}
```

### 6. Spring Modulith

모듈화된 아키텍처를 통해 독립적인 비즈니스 로직을 구현합니다.

> 상세 사용법은 [SPRING_MODULITH_GUIDE.md](origin/docs/SPRING_MODULITH_GUIDE.md) 및 [MODULE_COMMUNICATION_GUIDE.md](origin/docs/MODULE_COMMUNICATION_GUIDE.md) 참조

### 7. 암호화된 설정 관리

Jasypt를 사용한 민감 정보 암호화:

```yaml
spring:
  datasource:
    password: ENC(encrypted_password_here)
```

### 8. 데이터 암/복호화 서비스

```java
private final ICryptoService cryptoService;

// 암호화
String encrypted = cryptoService.encrypt("sensitive data");

// 복호화
String decrypted = cryptoService.decrypt(encrypted);
```

---

## 📚 API 문서

Swagger UI를 통해 API 문서를 확인할 수 있습니다:

```
http://localhost:7479/api.html
```

---

## 🧪 테스트

### 테스트 실행

```bash
cd origin

# 모든 테스트 실행
./gradlew test

# 테스트 및 커버리지 리포트 생성
./gradlew build
```

### 커버리지 리포트

테스트 커버리지 리포트는 다음 위치에서 확인할 수 있습니다:
- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

**목표 커버리지**: 100% (LINE 기준)

---

## ⚙️ 환경 설정

### application.yml 주요 설정

```yaml
config:
  options:
    use-default-async: true            # 비동기 실행자 사용 여부
    vault-type: native                 # Vault 타입 (native)
    auth-strategy: session             # 인증 전략 (session)
  constants:
    application-name: base-backend
    application-version: 0.0.1
    common-crypto-key: ${COMMON_CRYPTO_KEY}
    sse-emitter-timeout-ms: 60000      # SSE 타임아웃 (밀리초)

server:
  port: 7479                           # 서버 포트
  servlet:
    session:
      timeout: 30m                     # 세션 타임아웃

spring:
  jpa:
    open-in-view: false                # OSIV 비활성화
    hibernate:
      ddl-auto: validate               # DDL 자동 생성 모드

management:
  endpoints:
    web:
      base-path: /monitor              # Actuator 엔드포인트
      exposure:
        include: health, info

# i18n 설정
i18n:
  default-language: en                 # 기본 언어
  default-regulator: mfds              # 기본 규제기관
  resource-path: file:./config/i18n    # 리소스 파일 경로
  cache-enabled: true                  # 캐시 활성화
```

### 헬스 체크

```
http://localhost:7479/monitor/health
```

### 프로파일

지원되는 프로파일:
- `local`: 로컬 개발 환경
- `prod`: 프로덕션 (기본값)

## 📖 문서 가이드

프로젝트의 상세 가이드 문서는 `origin/docs/` 디렉토리에서 확인할 수 있습니다:

| 문서 | 설명 |
|------|------|
| [AUDIT_GUIDE.md](origin/docs/AUDIT_GUIDE.md) | 감사 로깅 시스템 사용 가이드 |
| [I18N_USAGE_GUIDE.md](origin/docs/I18N_USAGE_GUIDE.md) | 다국어(i18n) 사용 가이드 |
| [MODULE_COMMUNICATION_GUIDE.md](origin/docs/MODULE_COMMUNICATION_GUIDE.md) | 모듈 간 통신 가이드 |
| [PERMISSION_USAGE_GUIDE.md](origin/docs/PERMISSION_USAGE_GUIDE.md) | 권한 체크 시스템 사용 가이드 |
| [SPRING_MODULITH_GUIDE.md](origin/docs/SPRING_MODULITH_GUIDE.md) | Spring Modulith 가이드 |

---

## 🔍 추가 정보

### 공통 영역 수정

- 공통 영역(common)에 대한 추가 수정은 회의를 통해 결정합니다.

### QueryDSL 사용

QueryDSL Q클래스는 빌드 시 자동 생성됩니다:
```bash
./gradlew build
```

생성된 Q클래스는 `build/generated/sources/annotationProcessor/java/main/`에 위치합니다.

### 보안 관련 사항

- Native Vault 라이브러리는 플랫폼별로 빌드가 필요합니다:
   - Linux: `libnative_vault.so`
   - macOS: `libnative_vault.dylib`
   - Windows: `native_vault.dll`

### 문제 해결

**빌드 오류**
- Java 17 사용 여부 확인: `java -version`
- Gradle 캐시 정리: `./gradlew clean build --refresh-dependencies`

---

## 작성자

서강희

## 변경 이력

| 버전  | 날짜         | 변경 내역                                            |
|-----|------------|--------------------------------------------------|
| 1.0 | 2025-11-11 | 초안 작성                                            |
| 1.1 | 2025-12-17 | 프로젝트 구현 사항 업데이트<br>1. Spring Boot 4.0.0 적용<br>2. Spring Modulith 적용 |


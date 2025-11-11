# Base Backend

- Make Application Great Again
- Spring Boot 3.5 기반의 백엔드 애플리케이션 템플릿입니다.
- 재사용 가능한 공통 모듈과 확장 가능한 아키텍처를 제공합니다.


## 📋 목차

- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시작하기](#시작하기)
- [프로젝트 구조](#프로젝트-구조)
- [주요 기능 상세](#주요-기능-상세)
- [API 문서](#api-문서)
- [테스트](#테스트)
- [환경 설정](#환경-설정)

## 🚀 주요 기능

### Common 모듈 (공통 기능)
- **감사 로깅 시스템**: API 호출, 시스템 이벤트를 자동으로 추적 및 로깅
- **SSE (Server-Sent Events)**: 실시간 서버 푸시 알림 지원
- **비동기 처리**: 설정 가능한 비동기 작업 실행 환경
- **전역 예외 처리**: 통일된 예외 처리 및 응답 포맷
- **보안 기능**: 
  - Jasypt 기반 설정 암호화
  - Native Vault를 통한 민감 정보 관리
- **유틸리티**: JSON 처리, 날짜/시간, NanoID 생성 등

### Product 모듈 (비즈니스 로직)
- **역할 기반 접근 제어**: product 하위에 각 기능을 역할 기반으로 구현

## 🛠 기술 스택

### 프레임워크 & 언어
- **Java 17**
- **Spring Boot 3.5.7**
- **Gradle Kotlin DSL**

### 핵심 라이브러리
- **Spring Boot Starter Web**: RESTful API 개발
- **Spring Boot Starter Data JPA**: 데이터 영속성
- **QueryDSL 5.1.0**: 타입 안전 쿼리
- **MariaDB JDBC**: 데이터베이스 연결
- **Lombok**: 보일러플레이트 코드 감소

### 보안 & 암호화
- **Jasypt 3.0.5**: 설정 암호화
- **Native Vault**: JNI 기반 보안 저장소

### 문서화 & 테스트
- **SpringDoc OpenAPI 2.8.13**: API 문서 자동 생성
- **JUnit 5 & JaCoCo**: 테스트 및 커버리지

### 유틸리티
- **NanoID 1.0.1**: 고유 식별자 생성

## 📦 시작하기

### 사전 요구사항

- Java 17 이상
- Gradle 8.x
- MariaDB 10.x 이상

### 데이터베이스 설정

1. MariaDB 데이터베이스 생성 및 초기화:
```bash
mysql -u root -p < db-config/00_created_db.sql
```

2. 데이터베이스 스키마:
   - 데이터베이스명: `base_backend`

### 환경 변수 설정

프로젝트 루트의 상위 디렉토리에 `.env` 파일을 생성하세요:

```properties
# Application
APPLICATION_NAME=base-backend
APPLICATION_VERSION=0.0.1
APPLICATION_GROUP=com.kelly.base

# Server
APP_SERVER_PORT=7479

# Database
DATABASE_NAME=base_backend
DATABASE_SERVER_PORT=3306
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# JPA
JPA_DDL_AUTO=validate

# SSE
SSE_EMITTER_TIMEOUT_MS=60000

# Logging
KELLY_LOG_LEVEL=INFO
```

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/{APPLICATION_NAME}.jar
```

애플리케이션은 기본적으로 `http://localhost:7479`에서 실행됩니다.

## 📁 프로젝트 구조

```
src/main/java/com/kelly/base/
├── BaseBackendApplication.java          # 메인 애플리케이션
├── common/                              # 공통 모듈
│   ├── audit/                          # 감사 로깅
│   │   ├── advice/                     # Request/Response 인터셉터
│   │   ├── annotation/                 # 감사 관련 어노테이션
│   │   ├── dto/                        # 감사 로그 DTO
│   │   ├── listener/                   # 애플리케이션 생명주기 리스너
│   │   └── AuditLogService.java       # 감사 로그 서비스
│   ├── config/                         # 설정 클래스
│   │   ├── CommonAsyncConfig.java     # 비동기 처리 설정
│   │   ├── CommonJasyptConfig.java    # 암호화 설정
│   │   └── VaultConfig.java           # Vault 설정
│   ├── exception/                      # 예외 처리
│   │   ├── CommonException.java       # 공통 예외
│   │   └── CommonExceptionHandler.java # 전역 예외 핸들러
│   ├── interfaces/                     # 인터페이스 정의
│   ├── jni/                           # JNI Native 라이브러리
│   │   ├── InternalLibLoader.java     # 라이브러리 로더
│   │   └── NativeVault.java           # Native Vault 구현
│   ├── response/                       # 공통 응답 포맷
│   │   ├── CommonResponse.java        # 표준 응답 래퍼
│   │   └── CommonResultCode.java      # 결과 코드 정의
│   ├── sse/                           # Server-Sent Events
│   │   ├── dto/                       # SSE 이벤트 DTO
│   │   └── SseEmitterManager.java     # SSE 관리자
│   └── utils/                         # 유틸리티
│       ├── ConvertUtil.java           # 변환 유틸
│       ├── DateTimeUtil.java          # 날짜/시간 유틸
│       ├── JsonUtil.java              # JSON 유틸
│       └── ValueGenerator.java        # 값 생성 유틸
└── product/                            # 비즈니스 로직
    └── shared/                         # 공유 설정
        └── config/                     # Product 레벨 설정
```

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

### 2. SSE (Server-Sent Events)

실시간 서버 푸시 알림을 지원합니다.

```java
private final SseEmitterManager sseEmitterManager;

// 사용자에게 이벤트 전송
sseEmitterManager.sendToUser(userId : long, sseEvent : SseEvent);
```

### 3. 공통 응답 포맷

일관된 API 응답 구조를 제공합니다.

```json
{
  "success": true,
  "code": "S0000",
  "message": "Success",
  "data": { ... }
}
```

### 4. 비동기 처리

설정 기반 비동기 작업 실행을 지원합니다.

```yaml
config:
  options:
    use-default-async: true  # 공통 async executor 사용 여부
```

### 5. 암호화된 설정 관리

Jasypt를 사용한 민감 정보 암호화:

```yaml
spring:
  datasource:
    password: ENC(encrypted_password_here)
```

## 📚 API 문서

Swagger UI를 통해 API 문서를 확인할 수 있습니다:

```
http://localhost:7479/api.html
```

OpenAPI 스펙 JSON:
```
http://localhost:7479/v3/api-docs
```

## 🧪 테스트

### 테스트 실행

```bash
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

### 테스트 구조

```
src/test/java/com/kelly/base/
├── BaseBackendApplicationTests.java
└── common/
    ├── audit/                         # 감사 로깅 테스트
    ├── config/                        # 설정 테스트
    ├── exception/                     # 예외 처리 테스트
    ├── jni/                          # JNI 테스트
    ├── response/                      # 응답 포맷 테스트
    ├── sse/                          # SSE 테스트
    └── utils/                         # 유틸리티 테스트
```

## ⚙️ 환경 설정

### application.yml 주요 설정

```yaml
config:
  options:
    use-default-async: true            # 비동기 실행자 사용 여부
    vault-type: native                 # Vault 타입 (native)
  constants:
    application-name: base-backend
    application-version: 0.0.1
    sse-emitter-timeout-ms: 60000     # SSE 타임아웃 (밀리초)

server:
  port: 7479                           # 서버 포트

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
```

### 헬스 체크

```
http://localhost:7479/monitor/health
```

### 프로파일

지원되는 프로파일:
- `prod`: 프로덕션 (기본값)
- 기타 프로파일은 필요에 따라 추가 가능

## 🔍 추가 정보

### 공통 영역 수정

- 공통 영역(common)에 대한 추가 수정은 회의를 통해 결정합니다.

### QueryDSL 사용

QueryDSL Q클래스는 빌드 시 자동 생성됩니다:
```bash
./gradlew build
```

생성된 Q클래스는 `build/generated/sources/annotationProcessor/java/main/`에 위치합니다.

### 보안 고려사항

1. 프로덕션 환경에서는 강력한 데이터베이스 암호를 사용하세요
2. Jasypt 암호화 키를 안전하게 관리하세요
3. Native Vault 라이브러리는 플랫폼별로 빌드가 필요합니다:
   - Linux: `libnative_vault.so`
   - macOS: `libnative_vault.dylib`
   - Windows: `native_vault.dll`

### 문제 해결

**데이터베이스 연결 실패**
- MariaDB 서버가 실행 중인지 확인
- `.env` 파일의 데이터베이스 설정 확인
- 방화벽 설정 확인

**테스트 실패**
- H2 인메모리 데이터베이스가 테스트용으로 사용됨
- `src/test/resources/data.sql`이 테스트 데이터를 초기화함

**빌드 오류**
- Java 17 사용 여부 확인: `java -version`
- Gradle 캐시 정리: `./gradlew clean build --refresh-dependencies`

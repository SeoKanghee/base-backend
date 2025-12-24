# App Identity

인증/인가 기능에 특화된 경량 애플리케이션입니다 (Common + Identity 모듈만 포함).

---

## 목차

- [포함 모듈](#포함-모듈)
- [빌드 및 실행](#빌드-및-실행)
- [환경 설정](#환경-설정)
- [API 문서](#api-문서)
- [헬스 체크](#헬스-체크)

---

## 포함 모듈

| 모듈                                           | 설명                              |
|----------------------------------------------|---------------------------------|
| [Common](../../modules/common/README.md)     | 공통 기능 (감사 로깅, SSE, 암호화, i18n 등) |
| [Identity](../../modules/identity/README.md) | 인증/인가 (세션 기반 인증, 권한 체크)         |

---

## 빌드 및 실행

### 개발 환경 실행

```bash
./gradlew :apps:app-identity:bootRun
```

### JAR 빌드

```bash
./gradlew :apps:app-identity:bootJar
```

### JAR 실행

```bash
cd apps/identity
java -jar build/libs/app-identity.jar
```

### 빌드 결과물 위치

```
apps/identity/build/libs/app-identity.jar
```

---

## 환경 설정

### 기본 포트

- **서버 포트**: `7479`

### application.yml 주요 설정

```yaml
config:
    options:
        use-default-async: true        # 비동기 실행자 사용 여부
        vault-type: native             # Vault 타입 (native)
        auth-strategy: session         # 인증 전략 (session)
    constants:
        application-name: app-identity
        application-version: 0.0.1
        common-crypto-key: ${COMMON_CRYPTO_KEY}
        sse-emitter-timeout-ms: 60000  # SSE 타임아웃 (밀리초)

server:
    port: 7479
    servlet:
        session:
            timeout: 30m                 # 세션 타임아웃

spring:
    config:
        import: optional:file:../../.env[.properties]  # 루트 폴더의 .env 참조
    jpa:
        open-in-view: false            # OSIV 비활성화
        hibernate:
            ddl-auto: validate           # DDL 자동 생성 모드

# i18n 설정
i18n:
    default-language: en
    default-regulator: mfds
    resource-path: file:../../config/i18n
    cache-enabled: true
```

### .env 파일 참조

애플리케이션은 `../../.env` 경로로 루트 폴더의 환경 변수 파일을 참조합니다.

---

## API 문서

Swagger UI를 통해 API 문서를 확인할 수 있습니다:

```
http://localhost:7479/api.html
```

---

## 헬스 체크

```
http://localhost:7479/monitor/health
```

---

## 프로덕션 배포 구조

```
{설치 루트}/
├── .env                        # 환경 설정 파일
├── config/
│   └── i18n/                   # 다국어 리소스
└── apps/
    └── app-identity/
        └── app-identity.jar    # 실행 파일
```

**실행 방법:**

```bash
cd {설치 루트}/apps/app-identity
java -jar app-identity.jar
```

---

## Native Vault 라이브러리

Native 라이브러리는 `src/main/resources/native` 폴더에 포함되어 있습니다:

```
src/main/resources/native/
├── libnative_vault.dylib    # macOS
├── libnative_vault.so       # Linux
└── native_vault.dll         # Windows
```

> **참고**: 가능하면 애플리케이션 별로 다른 키를 사용하는 것을 추천합니다.

---

## 로그 파일

로그 파일은 `logs/` 디렉토리에 생성됩니다:

- `logs/app-identity.log`: 일반 애플리케이션 로그
- `logs/app-identity-audit.log`: 감사 로그

---

## 작성자

서강희

## 변경 이력

| 버전  | 날짜         | 변경 내역 |
|-----|------------|-------|
| 1.0 | 2025-12-24 | 초안 작성 |

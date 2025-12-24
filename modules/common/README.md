# Common 모듈

공통 기능을 제공하는 모듈입니다. 모든 다른 모듈에서 의존하여 사용할 수 있는 기반 기능들을 포함합니다.

---

## 목차

- [주요 기능](#주요-기능)
- [감사 로깅 시스템](#감사-로깅-시스템)
- [SSE (Server-Sent Events)](#sse-server-sent-events)
- [암호화 서비스](#암호화-서비스)
- [다국어 (i18n)](#다국어-i18n)
- [공통 응답 포맷](#공통-응답-포맷)
- [유틸리티](#유틸리티)
- [테스트](#테스트)

---

## 주요 기능

| 기능         | 설명                                  |
|------------|-------------------------------------|
| 감사 로깅      | API 호출, 시스템 이벤트를 자동으로 추적 및 로깅       |
| SSE        | 실시간 서버 푸시 알림 지원                     |
| 비동기 처리     | 설정 가능한 비동기 작업 실행 환경                 |
| 전역 예외 처리   | 통일된 예외 처리 및 응답 포맷                   |
| 보안 기능      | Jasypt 암호화, Native Vault, 데이터 암/복호화 |
| 다국어 (i18n) | 다국어 지원 메시지 서비스                      |
| 유틸리티       | JSON 처리, 날짜/시간, NanoID 생성 등         |

---

## 감사 로깅 시스템

모든 API 호출과 시스템 이벤트를 자동으로 로깅합니다.

### 기본 사용

모든 컨트롤러의 API 호출은 자동으로 감사 로깅됩니다.

### 감사 로깅 제외

특정 API에서 감사 로깅을 제외하려면 `@NoAudit` 어노테이션을 사용합니다:

```java

@NoAudit
@GetMapping("/public")
public ResponseEntity<?> publicEndpoint() {
    return ResponseEntity.ok("Public data");
}
```

### 로그 파일 위치

- `logs/{APPLICATION_NAME}.log`: 일반 애플리케이션 로그
- `logs/{APPLICATION_NAME}-audit.log`: 감사 로그

> 상세 사용법은 [AUDIT GUIDE](../../docs/AUDIT_GUIDE.md) 참조

---

## SSE (Server-Sent Events)

실시간 서버 푸시 알림을 지원합니다.

### 사용 방법

```java
private final SseEmitterManager sseEmitterManager;

// 사용자에게 이벤트 전송
SseEvent event = SseEvent.builder()
        .eventType(SseEventType.NOTIFICATION)
        .data(notificationData)
        .build();
sseEmitterManager.

sendToUser(userId, event);
```

### 설정

```yaml
config:
    constants:
        sse-emitter-timeout-ms: 60000  # SSE 타임아웃 (밀리초)
```

---

## 암호화 서비스

### Jasypt를 통한 설정 암호화

민감한 설정 값을 암호화하여 저장할 수 있습니다:

```yaml
spring:
    datasource:
        password: ENC(encrypted_password_here)
```

### 데이터 암/복호화 서비스

`ICryptoService`를 통해 데이터를 암/복호화할 수 있습니다:

```java
private final ICryptoService cryptoService;

// 암호화
String encrypted = cryptoService.encrypt("sensitive data");

// 복호화
String decrypted = cryptoService.decrypt(encrypted);
```

### Native Vault

JNI 기반의 보안 저장소를 제공합니다.

> **참고**: Native 라이브러리는 각 애플리케이션의 `resources/native` 폴더에 포함되어야 합니다.

---

## 다국어 (i18n)

다국어 메시지 지원을 제공합니다.

### 사용 방법

```java
private final II18nMessageService i18nMessageService;

// 현재 언어로 메시지 조회
String message = i18nMessageService.getMessage("error.not_found");

// 파라미터가 있는 메시지
String message = i18nMessageService.getMessage("welcome.user", new Object[]{"Kelly"});
```

### 메시지 파일 위치

- `config/i18n/common/`: 공통 메시지 (JSON 형식)
- `config/i18n/product/`: 제품별 메시지 (Properties 형식)

### 설정

```yaml
i18n:
    default-language: en            # 기본 언어
    default-regulator: mfds         # 기본 규제기관
    resource-path: file:../../config/i18n  # 리소스 파일 경로
    cache-enabled: true             # 캐시 활성화
```

> 상세 사용법은 [I18N USAGE GUIDE](../../docs/I18N_USAGE_GUIDE.md) 참조

---

## 공통 응답 포맷

일관된 API 응답 구조를 제공합니다.

### 응답 형식

```json
{
    "success": true,
    "code": "S0000",
    "message": "Success",
    "data": {
        ...
    }
}
```

---

## 유틸리티

### JSON 유틸리티

JSON 처리를 위한 유틸리티 클래스를 제공합니다.

### 날짜/시간 유틸리티

날짜 및 시간 처리를 위한 유틸리티 클래스를 제공합니다.

### NanoID

고유 식별자 생성을 위한 NanoID 유틸리티를 제공합니다.

---

## 테스트

```bash
# Common 모듈 테스트 실행
./gradlew :modules:common:test
```

### 커버리지 리포트

테스트 커버리지 리포트는 다음 위치에서 확인할 수 있습니다:

- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

---

## 패키지 구조

```
src/main/java/com/kelly/base/common/
├── audit/          # 감사 로깅
├── config/         # 설정 클래스
├── crypto/         # 암호화 서비스
├── exception/      # 예외 처리
├── i18n/           # 다국어
├── interfaces/     # 인터페이스 정의
├── jni/            # JNI Native 라이브러리
├── response/       # 공통 응답 포맷
├── sse/            # Server-Sent Events
└── utils/          # 유틸리티
```

---

## 작성자

서강희

## 변경 이력

| 버전  | 날짜         | 변경 내역 |
|-----|------------|-------|
| 1.0 | 2025-12-24 | 초안 작성 |

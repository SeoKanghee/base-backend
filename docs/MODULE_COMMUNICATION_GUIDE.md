# 모듈 간 통신 구현 가이드

## 개요

- Spring Modulith 적용에 따라 모듈 간 통신은 크게 두 가지 방식으로 구현할 수 있습니다
    - **Event 방식**: 비동기 이벤트 발행/구독
    - **API 방식**: 인터페이스를 통한 동기 호출
- 상황에 맞게 구현이 필요하여 가이드 문서를 작성합니다.

---

## Event 방식

### 구현 예제

```java
// 1. 이벤트 정의
public record LoginSuccessEvent(
                String loginId, String accountName, ZonedDateTime loginAt
        ) {
}

// 2. 이벤트 발행 (Publisher)
@Service
public class AuthService {
    private final ApplicationEventPublisher eventPublisher;

    public void login() {
        // 이벤트 발행 처리
        eventPublisher.publishEvent(new LoginSuccessEvent(...));
    }
}

// 3. 이벤트 구독 (Subscriber)
@Component
public class AuditEventListener {
    @ApplicationModuleListener
    void onLoginSuccess(LoginSuccessEvent event) {
        // 구독한 이벤트에 대한 처리
    }
}
```

### 장점

- 발행자(Publisher)는 구독자(Subscriber)를 몰라도 됩니다.
- 즉, 원칙적으로 새로운 구독자를 추가해도 발행자 코드에 대한 수정이 필요 없습니다.
- 발행자는 구독자의 처리를 기다리지 않습니다.
- 구독자가 구현한 개별 리스너가 독립된 트랜잭션에서 실행됩니다.
- 개별 리스너의 실패가 다른 리스너에 영향을 주지 않습니다.
- Spring Modulith 가 자동으로 재시도를 지원합니다.

### 단점

- 이벤트 처리 결과를 발행자가 받을 수 없습니다.
- 호출 흐름 추적이 복잡하고, 비동기 처리가 되어 로그 분석을 통한 디버깅이 어렵습니다.
- 일반적인 방법으로 통합 트랜잭션 처리가 불가능합니다.

---

## API 방식

### 구현 예제

```java
// 1. 인터페이스 정의 (api 패키지)
package com.kelly.base.identity.api;

public interface AccountApi {
    Optional<AccountDto> findByLoginId(String loginId);

    boolean isAccountActive(String loginId);
}

// 2. 인터페이스 구현
package com.kelly.base.identity.internal;

@Service
class AccountApiImpl implements AccountApi {
    private final AccountRepository accountRepository;

    @Override
    public Optional<AccountDto> findByLoginId(String loginId) {
        return accountRepository.findByLoginId(loginId).map(this::toDto);
    }
}

// 3. 다른 모듈에서 사용
@Service
public class OrderService {
    // 일반적인 의존성 주입 방식으로 처리
    private final AccountApi accountApi;
}
```

### 장점

- 기본적인 호출 및 응답 처리 방식으로 구현 및 로그 추척을 통한 디버깅이 쉽습니다.
- 개발자가 트랜잭션을 컨트롤하기 쉽습니다.
- 순환 의존성에 주의해야 하는 방식이지만, Spring Modulith 테스트 코드를 통한 대응이 가능합니다.

### 단점

- 모듈간 인터페이스에 대한 의존성 존재하는 방식입니다.
- api 패키지에 인터페이스를 노출 시킬 수 없는 경우, `package-info.java` 작성이 필요합니다.

---

## 비교 및 선택 가이드

### 빠른 선택 가이드

| 기준           | Event 방식     | API 방식        |
|--------------|--------------|---------------|
| **응답 필요 여부** | ❌ 불필요        | ✅ 필요          |
| **즉시성**      | ⚠️ 비동기       | ✅ 즉시          |
| **결합도**      | ✅ 낮음 (Loose) | ⚠️ 있음 (Tight) |
| **성능**       | ✅ 높음 (비동기)   | ⚠️ 낮음 (동기)    |
| **트랜잭션**     | ⚠️ 분리됨       | ✅ 통합 가능       |
| **디버깅**      | ⚠️ 어려움       | ✅ 쉬움          |
| **확장성**      | ✅ 높음         | ⚠️ 낮음         |

---

## 작성자

서강희

## 변경 이력

| 버전  | 날짜         | 변경 내역 |     |
|-----|------------|-------|-----|
| 1.0 | 2025-12-16 | 초안 작성 |     |

# Identity 모듈

인증 및 인가 기능을 제공하는 모듈입니다. 세션 기반 인증, 계정 관리, 역할 기반 접근 제어를 지원합니다.

---

## 목차

- [주요 기능](#주요-기능)
- [인증 시스템](#인증-시스템)
- [권한 체크 시스템](#권한-체크-시스템)
- [계정 관리](#계정-관리)
- [테스트](#테스트)

---

## 주요 기능

| 기능          | 설명                               |
|-------------|----------------------------------|
| 세션 기반 인증    | Spring Security를 활용한 세션 기반 인증/인가 |
| 계정 관리       | 사용자 계정 생성, 수정, 삭제                |
| 역할 기반 접근 제어 | 역할(Role) 기반의 리소스 접근 제어           |
| 권한 체크 시스템   | 어노테이션 기반의 권한 검증                  |

---

## 인증 시스템

### 인증 전략

세션 기반 인증을 사용합니다.

```yaml
config:
    options:
        auth-strategy: session  # 인증 전략
```

### 세션 설정

```yaml
server:
    servlet:
        session:
            timeout: 30m  # 세션 타임아웃
```

---

## 권한 체크 시스템

어노테이션 기반의 권한 검증을 지원합니다.

### 단일 권한 체크

특정 권한이 있는 사용자만 접근 가능하도록 설정:

```java

@RequirePermission(value = "USER_READ", operator = PermOperator.AND)
@GetMapping("/users")
public ResponseEntity<?> getUsers() {
    return ResponseEntity.ok(userService.getAll());
}
```

### 다중 권한 체크 (AND)

모든 권한이 있어야 접근 가능:

```java

@RequirePermission(value = {"ADMIN", "USER_WRITE"}, operator = PermOperator.AND)
@PostMapping("/users")
public ResponseEntity<?> createUser(@RequestBody UserDto user) {
    return ResponseEntity.ok(userService.create(user));
}
```

### 다중 권한 체크 (OR)

여러 권한 중 하나라도 있으면 접근 가능:

```java

@RequirePermission(value = {"ADMIN", "MANAGER"}, operator = PermOperator.OR)
@DeleteMapping("/users/{id}")
public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.ok().build();
}
```

### PermOperator 옵션

| 연산자   | 설명                      |
|-------|-------------------------|
| `AND` | 지정된 모든 권한이 있어야 접근 가능    |
| `OR`  | 지정된 권한 중 하나라도 있으면 접근 가능 |

> 상세 사용법은 [PERMISSION USAGE GUIDE](../../docs/PERMISSION_USAGE_GUIDE.md) 참조

---

## 계정 관리

### 계정 API

계정 생성, 조회, 수정, 삭제 기능을 제공합니다.

### 도메인 엔티티

- Account: 사용자 계정 정보
- Role: 역할 정보
- Permission: 권한 정보

---

## 테스트

```bash
# Identity 모듈 테스트 실행
./gradlew :modules:identity:test
```

### 커버리지 리포트

테스트 커버리지 리포트는 다음 위치에서 확인할 수 있습니다:

- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

---

## 패키지 구조

```
src/main/java/com/kelly/base/identity/
├── accounts/       # 계정 관리
├── auth/           # 인증
├── internal/       # 내부 구현
├── PermissionCheckAspect.java   # 권한 체크 AOP
├── PermOperator.java            # 권한 연산자
└── RequirePermission.java       # 권한 체크 어노테이션
```

---

## 의존성

이 모듈은 다음 모듈에 의존합니다:

- `modules/common`: 공통 기능 모듈

---

## 작성자

서강희

## 변경 이력

| 버전  | 날짜         | 변경 내역 |
|-----|------------|-------|
| 1.0 | 2025-12-24 | 초안 작성 |
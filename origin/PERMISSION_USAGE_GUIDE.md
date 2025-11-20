# Permission 기반 인증 시스템 사용 가이드

## 📋 개요

Enum 기반에서 DB 기반 Role/Permission 시스템으로 전환되었습니다.

## 🏗️ 아키텍처

### DB 구조
- `role`: 역할 정보 (code: "ROLE_SITE_MANAGER")
- `permission`: 권한 정보 (code: "VIEW_MY_ACCOUNT")
- `role_permission`: 역할-권한 매핑 (N:M)
- `account`: 사용자 정보 (role: role.code 저장)

### Spring Security 통합
사용자가 로그인하면:
1. `CustomUserDetailsService`가 Account 조회
2. Account의 role(code)로 Role 조회
3. Role의 ID로 연결된 모든 Permission 조회 (**캐싱됨**)
4. `CustomUserDetails`에 Role code + Permission codes를 authorities로 설정
5. 세션에 저장되어 이후 요청에서 재사용

예시 authorities:
```
["ROLE_SITE_MANAGER", "VIEW_MY_ACCOUNT", "VIEW_ACCOUNT_LIST", "MANAGE_ACCOUNT"]
```

### 성능 최적화 (캐싱)
- **로그인 시**: 세션이 없으면 DB 조회 (Account 1회 + Role 1회 + Permissions 1회)
- **로그인 후**: 세션에서 재사용, DB 조회 없음
- **Permission 캐싱**: 같은 Role을 가진 첫 사용자만 DB 조회, 이후는 캐시 사용
- 예: ROLE_GENERAL_USER를 가진 100명이 로그인해도 Permissions는 1회만 조회

## 🔧 사용 방법

### 1. 상수 클래스 사용

**RoleCode.java**:
```java
public final class RoleCode {
    public static final String SITE_MANAGER = "ROLE_SITE_MANAGER";
    public static final String SERVICE_ENGINEER = "ROLE_SERVICE_ENGINEER";
    public static final String ADVANCED_USER = "ROLE_ADVANCED_USER";
    public static final String GENERAL_USER = "ROLE_GENERAL_USER";
    public static final String DEMO_USER = "ROLE_DEMO_USER";
}
```

**PermissionCode.java**:
```java
public final class PermissionCode {
    public static final String VIEW_MY_ACCOUNT = "VIEW_MY_ACCOUNT";
    public static final String VIEW_ACCOUNT_LIST = "VIEW_ACCOUNT_LIST";
    public static final String MANAGE_ACCOUNT = "MANAGE_ACCOUNT";
    // TODO: 추가 권한 정의 (총 28개 예상)
}
```

### 2. 커스텀 어노테이션 사용

**단일 권한 체크**:
```java
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @RequirePermission(PermissionCode.VIEW_ACCOUNT_LIST)
    @GetMapping
    public List<Account> getAccounts() {
        // VIEW_ACCOUNT_LIST 권한이 있어야 접근 가능
        return accountService.getAccounts();
    }
    
    @RequirePermission(PermissionCode.MANAGE_ACCOUNT)
    @PostMapping
    public Account createAccount(@RequestBody AccountRequest request) {
        // MANAGE_ACCOUNT 권한이 있어야 접근 가능
        return accountService.createAccount(request);
    }
}
```

**여러 권한 체크 (OR 연산)**:
```java
@RequirePermission(value = {
    PermissionCode.MANAGE_ACCOUNT, 
    "SYSTEM_ADMIN"
}, operator = LogicalOperator.OR)
@DeleteMapping("/{id}")
public void deleteAccount(@PathVariable Long id) {
    // MANAGE_ACCOUNT 또는 SYSTEM_ADMIN 권한 중 하나만 있어도 OK
    accountService.deleteAccount(id);
}
```

**여러 권한 체크 (AND 연산)**:
```java
@RequirePermission(value = {
    PermissionCode.MANAGE_ACCOUNT, 
    "SYSTEM_ADMIN"
}, operator = LogicalOperator.AND)
@PostMapping("/dangerous-operation")
public void dangerousOperation() {
    // MANAGE_ACCOUNT AND SYSTEM_ADMIN 둘 다 있어야 OK
    accountService.dangerousOperation();
}
```

### 3. Spring Security 기본 어노테이션 사용

**Role 체크**:
```java
@PreAuthorize("hasRole('SITE_MANAGER')")
@DeleteMapping("/system/reset")
public void resetSystem() {
    // ROLE_SITE_MANAGER 역할이 있어야 접근 가능
    systemService.reset();
}
```

**Permission 체크**:
```java
@PreAuthorize("hasAuthority('VIEW_ACCOUNT_LIST')")
@GetMapping("/accounts")
public List<Account> getAccounts() {
    // VIEW_ACCOUNT_LIST 권한이 있어야 접근 가능
    return accountService.getAccounts();
}
```

**복합 조건**:
```java
@PreAuthorize("hasRole('SITE_MANAGER') or hasAuthority('MANAGE_ACCOUNT')")
@PostMapping("/accounts")
public Account createAccount(@RequestBody AccountRequest request) {
    // ROLE_SITE_MANAGER 역할 또는 MANAGE_ACCOUNT 권한이 있어야 OK
    return accountService.createAccount(request);
}
```

### 4. 프로그래밍 방식 권한 체크

```java
@Service
public class SomeService {
    
    public void someMethod() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // 권한 체크
        boolean hasPermission = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(a -> a.equals(PermissionCode.MANAGE_ACCOUNT));
        
        if (!hasPermission) {
            throw new AccessDeniedException("권한이 없습니다");
        }
        
        // 비즈니스 로직
    }
}
```

## 📝 권한 추가 방법

### 1. DB에 Permission 추가
```sql
INSERT INTO permission (code, name, description, bit_index) VALUES
    ('NEW_PERMISSION', '새로운 권한', '새로운 권한 설명', 3);
```

### 2. PermissionCode 상수 추가
```java
public final class PermissionCode {
    // ...
    public static final String NEW_PERMISSION = "NEW_PERMISSION";
}
```

### 3. Role에 Permission 연결
```sql
INSERT INTO role_permission (role_id, permission_id)
    SELECT r.id, p.id
    FROM role r
    CROSS JOIN permission p
    WHERE r.code = 'ROLE_SITE_MANAGER'
        AND p.code = 'NEW_PERMISSION';
```

### 4. 코드에서 사용
```java
@RequirePermission(PermissionCode.NEW_PERMISSION)
@GetMapping("/new-feature")
public void newFeature() {
    // ...
}
```

## 🎯 권장 사항

1. **Permission 중심 설계**: 가능한 Permission을 사용하여 세밀한 권한 제어
2. **Role 레벨 체크**: 넓은 범위의 권한이 필요한 경우에만 Role 사용
3. **상수 클래스 활용**: 오타 방지를 위해 PermissionCode 상수 사용
4. **문서화**: 새로운 권한 추가 시 이 문서 업데이트

## 🚨 주의사항

1. **DB와 동기화**: PermissionCode 상수는 DB의 permission.code와 일치해야 함
2. **권한 없음 예외**: `AccessDeniedException`이 발생하면 403 Forbidden 응답
3. **테스트**: 권한 체크 로직은 반드시 테스트 코드 작성

## 🔄 마이그레이션 체크리스트

- [x] DB 스키마 수정 (role.code에 ROLE_ prefix)
- [x] Role, Permission, RolePermission 엔티티 생성
- [x] Repository 생성
- [x] Account 엔티티 수정 (enum → String)
- [x] RoleCode, PermissionCode 상수 클래스 생성
- [x] CustomUserDetails 수정
- [x] CustomUserDetailsService 수정
- [x] @RequirePermission 어노테이션 구현
- [x] PermissionCheckAspect 구현
- [x] AccountRole enum 삭제
- [ ] 테스트 코드 작성 (CustomUserDetailsTests 등)
- [ ] 추가 Permission 정의 (28개 목표)

## 📞 문의

궁금한 사항이나 이슈가 있으면 개발팀에 문의하세요.

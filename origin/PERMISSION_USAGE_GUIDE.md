# Permission 기반 인증 사용 가이드

## 📋 개요

Permission 구현 내용에 대한 설명입니다.

## 🏗️ 아키텍처

### DB 구조
- `role` 테이블: 역할 정보
  - `id`: Primary Key
  - `code`: 역할 코드 (예: "ROLE_SITE_MANAGER")
  - `name`: 역할 이름
  - `description`: 역할 설명
  
- `permission` 테이블: 권한 정보
  - `id`: Primary Key
  - `code`: 권한 코드 (예: "VIEW_MY_ACCOUNT")
  - `name`: 권한 이름
  - `description`: 권한 설명
  
- `role_permission` 테이블: 역할-권한 매핑 (N:M)
  - `id`: Primary Key
  - `role_id`: FK → role.id
  - `permission_id`: FK → permission.id
  
- `account` 테이블: 사용자 정보
  - `role`: role.code 값을 String으로 저장

### Spring Security 통합
사용자가 로그인하면:
1. `CustomUserDetailsService`가 Account 조회
2. Account의 role(code)로 Role 조회
3. Role의 ID로 연결된 모든 Permission 조회
4. `CustomUserDetails`에 Role code + Permission codes를 authorities로 설정
5. 세션에 저장되어 이후 요청에서 재사용

예시 authorities:
```
["ROLE_SITE_MANAGER", "VIEW_MY_ACCOUNT", "VIEW_ACCOUNT_LIST", "MANAGE_ACCOUNT"]
```

## 🔧 사용 방법

### 1. 상수 클래스 사용

**Constants.java** :
```java
package com.kelly.base.product.shared;

public final class Constants {
    
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RoleCode {
        public static final String ROLE_SITE_MANAGER = "ROLE_SITE_MANAGER";
        public static final String ROLE_SERVICE_ENGINEER = "ROLE_SERVICE_ENGINEER";
        public static final String ROLE_ADVANCED_USER = "ROLE_ADVANCED_USER";
        public static final String ROLE_GENERAL_USER = "ROLE_GENERAL_USER";
        public static final String ROLE_DEMO_USER = "ROLE_DEMO_USER";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PermissionCode {
        public static final String VIEW_MY_ACCOUNT = "VIEW_MY_ACCOUNT";
        public static final String VIEW_ACCOUNT_LIST = "VIEW_ACCOUNT_LIST";
        public static final String MANAGE_ACCOUNT = "MANAGE_ACCOUNT";
    }
}
```

**사용 예시**:
```java
import static com.kelly.base.product.shared.Constants.RoleCode;
import static com.kelly.base.product.shared.Constants.PermissionCode;

// 사용
String role = RoleCode.ROLE_SITE_MANAGER;
String permission = PermissionCode.VIEW_MY_ACCOUNT;
```

### 2. 커스텀 어노테이션 사용 (권장)

**어노테이션 import**:
```java
import com.kelly.base.product.shared.permission.annotation.RequirePermission;
import com.kelly.base.product.shared.permission.PermOperator;
import static com.kelly.base.product.shared.Constants.PermissionCode;
```

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

**여러 권한 체크 (OR 연산)** - 기본값:
```java
@RequirePermission(value = {
    PermissionCode.MANAGE_ACCOUNT, 
    "SYSTEM_ADMIN"
}, operator = PermOperator.OR)
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
}, operator = PermOperator.AND)
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
import static com.kelly.base.product.shared.Constants.PermissionCode;

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

### 2. Constants.PermissionCode에 상수 추가 (옵션)
`src/main/java/com/kelly/base/product/shared/Constants.java` 파일을 수정:
```java
public static final class PermissionCode {
    public static final String VIEW_MY_ACCOUNT = "VIEW_MY_ACCOUNT";
    public static final String VIEW_ACCOUNT_LIST = "VIEW_ACCOUNT_LIST";
    public static final String MANAGE_ACCOUNT = "MANAGE_ACCOUNT";
    public static final String NEW_PERMISSION = "NEW_PERMISSION";  // 추가
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
import static com.kelly.base.product.shared.Constants.PermissionCode;

@RequirePermission(PermissionCode.NEW_PERMISSION)
@GetMapping("/new-feature")
public void newFeature() {
    // ...
}
```


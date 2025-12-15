# i18n 사용 가이드

## 개요

i18n 을 통해 사용자별 언어 설정과 규제기관에 따라 다국어 메시지를 제공합니다.

## 주요 특징

- **사용자별 언어**: Account 테이블의 `language_code`로 사용자별 언어 적용
- **시스템 규제기관**: application.yml에서 규제기관 설정 (fda, ce 등)
- **외부 리소스**: JAR 외부의 `./config/i18n` 디렉터리에서 메시지 로드
- **동적 리로드**: 애플리케이션 재시작 없이 메시지 변경 가능
- **계층적 조회**: 프로덕트 리소스 → 공통 리소스 → fallback

## 디렉터리 구조

```
프로젝트 루트/
├── base-backend.jar
└── config/
    └── i18n/
        ├── common/                    # 공통 메시지 (JSON)
        │   ├── messages_en.json
        │   └── messages_ko.json
        └── product/                   # 프로덕트 메시지 (Properties)
            ├── messages_fda_en.properties
            ├── messages_fda_ko.properties
            ├── messages_mfds_en.properties
            └── messages_mfds_ko.properties
```

## 메시지 키 규칙

### 공통 메시지 (common)
- 접두사: `TID_`
- 포맷: `TID_{5자리 숫자}`
- 예시: `TID_00001`, `TID_00002`
- 파일: `config/i18n/common/messages_{언어}.json`

### 사용자 정의 메시지 (product)
- 접두사: `EXT_TID_`
- 포맷: `EXT_TID_{5자리 숫자}`
- 예시: `EXT_TID_00001`, `EXT_TID_00002`
- 파일: `config/i18n/product/messages_{규제기관}_{언어}.properties`

## 사용 방법

### 1. Service/Controller에서 메시지 조회

```java
@Service
@RequiredArgsConstructor
public class SomeService {
    
    private final I18nMessageService i18nMessageService;
    
    public void someMethod() {
        // 1️⃣ 현재 로그인한 사용자의 언어로 조회 (단순 조회)
        String message = i18nMessageService.getMessage("TID_00001");
        // 결과: "저장" (사용자 languageCode가 "ko"인 경우)
        
        // 2️⃣ 현재 사용자 언어로 메시지 포맷팅
        String welcome = i18nMessageService.getMessage("TID_WELCOME", userName);
        // messages_ko.json: "TID_WELCOME": "{0}님, 환영합니다"
        // 결과: "홍길동님, 환영합니다"
        
        // 3️⃣ 규제기관별 메시지 조회
        String role = i18nMessageService.getMessage("EXT_TID_00001");
        // messages_fda_ko.properties: EXT_TID_00001=사이트 관리자
        // 결과: "사이트 관리자"
        
        // 4️⃣ 복수 파라미터 포맷팅
        String notification = i18nMessageService.getMessage("TID_NOTIFY", userName, actionName);
        // messages_ko.json: "TID_NOTIFY": "{0}님이 {1} 작업을 수행했습니다"
        // 결과: "홍길동님이 업데이트 작업을 수행했습니다"
    }
}
```

**메서드 요약**:
- `getMessage(key)`: 현재 로그인한 사용자의 언어와 시스템 규제기관으로 메시지 조회
- `getMessage(key, args...)`: 현재 언어로 조회 + MessageFormat을 사용한 파라미터 포맷팅

### 2. 사용자 언어 설정

사용자의 언어는 `Account` 테이블의 `language_code` 컬럼에 저장됩니다.

```java
// Account 엔티티
@Column(name = "language_code", nullable = false)
private String languageCode = "en";  // 기본값: 영어
```

언어 변경:
```java
Account account = accountRepository.findById(accountId).orElseThrow();
// Account 엔티티에 언어 변경 메서드 추가 필요
account.changeLanguage("ko");
accountRepository.save(account);
```

### 3. 시스템 규제기관 설정

`application.yml` 또는 환경 변수로 설정:

```yaml
i18n:
  default-regulator: fda  # fda, mfds 등
```

환경 변수:
```bash
export I18N_DEFAULT_REGULATOR=mfds
```

### 4. 비로그인 사용자

SecurityContext에 인증 정보가 없는 경우 기본 언어 사용:

```yaml
i18n:
  default-language: en  # 비로그인 사용자의 기본 언어
```

## 메시지 리소스 파일 작성

### 공통 리소스 (JSON)

`config/i18n/common/messages_en.json`:
```json
{
  "TID_00001": "Save",
  "TID_00002": "Yes",
  "TID_00003": "No"
}
```

`config/i18n/common/messages_ko.json`:
```json
{
  "TID_00001": "저장",
  "TID_00002": "예",
  "TID_00003": "아니오"
}
```

### 프로덕트 리소스 (properties + UTF-8)

**✨ 주요 특징**: properties 파일은 주석(`#`) 활용이 가능합니다

`config/i18n/product/messages_fda_en.properties`:
```properties
# ===================================
# Product Messages (English + FDA)
# ===================================

# -----------------------------------
# User Roles
# -----------------------------------
EXT_TID_00001=Site Manager
EXT_TID_00002=Service Manager
```

`config/i18n/product/messages_fda_ko.properties`:
```properties
# ===================================
# 프로덕트 메시지 리소스 (한국어 + FDA)
# ===================================

# -----------------------------------
# 사용자 역할 (User Roles)
# -----------------------------------
EXT_TID_00001=사이트 관리자
EXT_TID_00002=서비스 관리자
```

## 동적 메시지 리로드

메시지 파일 수정 후 애플리케이션 재시작 없이 반영:

### API 호출
```bash
POST /api/system/i18n/reload
Authorization: Bearer {token}
```

### 필요 권한
- `MANAGE_SYSTEM` 권한 필요

### 사용 시나리오
1. `config/i18n/common/messages_ko.json` 파일 수정
2. `POST /api/system/i18n/reload` API 호출
3. 즉시 변경된 메시지 반영

## 새로운 언어 추가

리소스 파일만 생성하면 자동으로 인식됩니다.

```bash
# 일본어 추가 예시
config/i18n/common/messages_ja.json
config/i18n/product/messages_fda_ja.properties
config/i18n/product/messages_mfds_ja.properties
```

애플리케이션 재시작 시 자동으로 로드됩니다.

## 새로운 규제기관 추가

리소스 파일만 생성하고 시스템 설정을 변경하면 됩니다.

### 1. 리소스 파일 생성

```bash
# CE 규제기관 추가 예시
config/i18n/product/messages_ce_en.properties
config/i18n/product/messages_ce_ko.properties
```

### 2. 시스템 설정 변경

```yaml
i18n:
  default-regulator: ce
```

**💡 참고**: `I18nMessageSourceConfig`가 `config/i18n/product/` 디렉터리를 스캔하여 `messages_{규제기관}_{언어}.properties` 패턴의 파일을 자동으로 로드합니다.

## 메시지 조회 우선순위

메시지 조회는 다음 순서로 진행됩니다:

1. **규제기관별 리소스**: `messages_{규제기관}_{언어}.properties`
2. **공통 리소스**: `messages_{언어}.json`
3. **Locale 폴백**: Spring MessageSource의 기본 폴백 (예: ko → en)
4. **기본값**: 메시지 키 자체 반환

### 조회 예시

**사용자 언어: ko, 규제기관: fda, 키: EXT_TID_00001**

```
1. messages_fda_ko.properties 에서 EXT_TID_00001 조회
   → 성공 시 반환
   
2. messages_ko.json 에서 EXT_TID_00001 조회
   → 성공 시 반환
   
3. Locale 폴백: messages_fda_en.properties 에서 조회
   → 성공 시 반환
   
4. "EXT_TID_00001" 반환 (키 자체)
```

**💡 참고**: `fallbackToSystemLocale`이 false로 설정되어 있어, 시스템 로케일이 아닌 설정된 기본 언어(default-language)로 폴백됩니다.

## 환경별 설정

### 로컬 개발 환경
```yaml
# src/main/resources/application-local.yml
i18n:
  resource-path: file:./config/i18n
  default-language: ko
  default-regulator: mfds
```

### 프로덕션 환경
```yaml
# src/main/resources/application-prod.yml
i18n:
  resource-path: file:./config/i18n
  default-language: en
  default-regulator: fda
```

환경 변수로 오버라이드:
```bash
export I18N_RESOURCE_PATH=file:/app/config/i18n
export I18N_DEFAULT_REGULATOR=ce
```

## 참고사항

- **Thread Safety**: I18nContext는 ThreadLocal 사용으로 스레드 안전
- **성능**: 모든 메시지는 애플리케이션 시작 시 메모리에 캐싱
- **메모리**: 메시지 파일이 커질 경우, 로딩 전략 수정이 필요

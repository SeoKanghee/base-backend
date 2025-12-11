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
            ├── messages_en_fda.properties
            ├── messages_ko_fda.properties
            ├── messages_en_ce.properties
            └── messages_ko_ce.properties
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
- 파일: `config/i18n/product/messages_{언어}_{규제기관}.properties`

## 사용 방법

### 1. Service/Controller에서 메시지 조회

```java
@Service
@RequiredArgsConstructor
public class SomeService {
    
    private final I18nMessageService i18nMessageService;
    
    public void someMethod() {
        // 1️⃣ 현재 로그인한 사용자의 언어로 조회
        String message = i18nMessageService.getMessage("TID_00001");
        // 결과: "저장" (사용자 languageCode가 "ko"인 경우)
        
        // 2️⃣ 명시적으로 언어/규제기관 지정
        String role = i18nMessageService.getMessageWith("EXT_TID_00001", "en", "ce");
        // 결과: "Site Manager"
        
        // 3️⃣ 현재 사용자 언어로 포맷팅
        String welcome = i18nMessageService.formatMessage("TID_WELCOME", userName);
        // messages_ko.json: "TID_WELCOME": "{0}님, 환영합니다"
        // 결과: "홍길동님, 환영합니다"
        
        // 4️⃣ 언어/규제 지정 + 포맷팅
        String notification = i18nMessageService.formatMessageWith(
            "TID_NOTIFY", "en", "fda", userName, actionName
        );
        // messages_en_fda.properties: TID_NOTIFY={0} performed {1}
        // 결과: "John performed Update"
    }
}
```

**메서드 요약**:
- `getMessage(key)`: 현재 컨텍스트 언어로 조회
- `getMessageWith(key, lang, reg)`: 언어/규제 명시하여 조회
- `formatMessage(key, args...)`: 현재 언어로 조회 + 포맷팅
- `formatMessageWith(key, lang, reg, args...)`: 언어/규제 명시 + 포맷팅

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
  regulatory: fda  # fda, ce, mfds 등
```

환경 변수:
```bash
export I18N_REGULATORY=ce
java -jar base-backend.jar
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

### 프로덕트 리소스 (Properties + UTF-8)

**✨ 주요 특징**: Properties 파일은 **UTF-8 인코딩**으로 읽히므로, **한글을 직접 작성**하고 **주석(`#`)을 사용**할 수 있습니다!

`config/i18n/product/messages_en_fda.properties`:
```properties
# ===================================
# Product Messages (English + FDA)
# ===================================

# -----------------------------------
# User Roles
# -----------------------------------

# Site Manager: Top administrator with all system permissions
EXT_TID_00001=Site Manager

# Service Manager: Administrator with service operation and user management permissions
EXT_TID_00002=Service Manager
```

`config/i18n/product/messages_ko_fda.properties`:
```properties
# ===================================
# 프로덕트 메시지 리소스 (한국어 + FDA)
# ===================================

# -----------------------------------
# 사용자 역할 (User Roles)
# -----------------------------------

# 사이트 관리자: 시스템의 모든 권한을 가진 최고 관리자
# Site Manager: Top administrator with all system permissions
EXT_TID_00001=사이트 관리자

# 서비스 관리자: 서비스 운영 및 사용자 관리 권한을 가진 관리자
# Service Manager: Administrator with service operation and user management permissions
EXT_TID_00002=서비스 관리자
```

**💡 주석 활용 팁**:
- `#`으로 시작하는 줄은 주석으로 처리됨
- 메시지 키의 의미, 사용 위치, 주의사항 등을 주석으로 명시
- 한국어와 영어 주석을 함께 작성하여 국제 협업 지원
- 섹션 구분자(`===`, `---`)로 가독성 향상

## 동적 메시지 리로드

메시지 파일 수정 후 애플리케이션 재시작 없이 반영:

### API 호출
```bash
POST /api/admin/i18n/reload
Authorization: Bearer {token}
```

### 필요 권한
- `MANAGE_SYSTEM` 권한 필요

### 사용 시나리오
1. `config/i18n/common/messages_ko.json` 파일 수정
2. `/api/admin/i18n/reload` API 호출
3. 즉시 변경된 메시지 반영

## 새로운 언어 추가

### 1. 리소스 파일 생성

```bash
# 일본어 추가 예시
config/i18n/common/messages_ja.json
config/i18n/product/messages_ja_fda.properties
config/i18n/product/messages_ja_ce.properties
```

### 2. I18nMessageServiceImpl 수정

```java
// 지원 언어 목록에 추가
private void loadAllMessages() {
    final String[] languages = {"en", "ko", "ja"};  // ja 추가
    // ...
}
```

## 새로운 규제기관 추가

### 1. 리소스 파일 생성

```bash
# MFDS 추가 예시
config/i18n/product/messages_en_mfds.properties
config/i18n/product/messages_ko_mfds.properties
```

### 2. I18nMessageServiceImpl 수정

```java
// 지원 규제기관 목록에 추가
private void loadAllMessages() {
    final String[] regulatories = {"fda", "ce", "mfds"};  // mfds 추가
    // ...
}
```

### 3. 시스템 설정 변경

```yaml
i18n:
  regulatory: mfds
```

## 메시지 조회 우선순위

1. **프로덕트 리소스**: `messages_{언어}_{규제기관}.properties`
2. **공통 리소스**: `messages_{언어}.json`
3. **폴백**: 기본 언어로 재시도 (fallback-enabled: true인 경우)
4. **기본값**: 메시지 키 자체 반환

예시:
```
사용자 언어: ko, 규제기관: fda, 키: EXT_TID_00001

1. messages_ko_fda.properties 조회 → 성공 시 반환
2. messages_ko.json 조회 → 성공 시 반환
3. messages_en_fda.properties 조회 (폴백) → 성공 시 반환
4. messages_en.json 조회 (폴백) → 성공 시 반환
5. "EXT_TID_00001" 반환 (키 자체)
```

## 환경별 설정

### 로컬 개발 환경
```yaml
# src/main/resources/application-local.yml
i18n:
  resource-path: file:./config/i18n
  default-language: ko
  regulatory: fda
```

### 프로덕션 환경
```yaml
# src/main/resources/application-prod.yml
i18n:
  resource-path: file:./config/i18n
  default-language: en
  regulatory: fda
```

환경 변수로 오버라이드:
```bash
export I18N_RESOURCE_PATH=file:/app/config/i18n
export I18N_REGULATORY=ce
```

## 트러블슈팅

### 1. 메시지를 찾을 수 없음

**증상**: 메시지 키가 그대로 표시됨

**원인**:
- 리소스 파일이 없거나 경로가 잘못됨
- 메시지 키가 리소스 파일에 없음

**해결**:
1. `config/i18n` 디렉터리 구조 확인
2. 로그에서 "Required i18n resource not found" 확인
3. 메시지 키 철자 확인

### 2. 한글이 깨짐 (Properties 파일)

**원인**: Properties 파일 인코딩 문제

**해결**:
```bash
# native2ascii 사용
native2ascii -encoding UTF-8 messages_ko_fda.properties messages_ko_fda_encoded.properties

# 또는 Java에서 UTF-8로 직접 로드 (현재 구현은 UTF-8 사용)
```

### 3. 애플리케이션 시작 실패

**증상**: "Required i18n resource not found" 에러

**해결**:
1. 필수 리소스 파일 존재 확인:
   - `messages_en.json`
   - `messages_ko.json`
   - `messages_en_fda.properties`
   - `messages_ko_fda.properties`
   - `messages_en_ce.properties`
   - `messages_ko_ce.properties`

2. 경로 확인:
   ```bash
   ls -la ./config/i18n/common/
   ls -la ./config/i18n/product/
   ```

## 베스트 프랙티스

1. **메시지 키 관리**: 스프레드시트나 별도 문서로 메시지 키 목록 관리
2. **일관된 네이밍**: 도메인별로 키 그룹핑 (예: `TID_AUTH_xxx`, `TID_USER_xxx`)
3. **폴백 전략**: fallback-enabled를 true로 설정하여 안정성 확보
4. **버전 관리**: 리소스 파일을 Git으로 관리
5. **테스트**: 각 언어/규제기관 조합에 대한 메시지 존재 여부 테스트
6. **문서화**: 새로운 메시지 키 추가 시 문서 업데이트

## 참고사항

- **Thread Safety**: I18nContext는 ThreadLocal 사용으로 스레드 안전
- **성능**: 모든 메시지는 애플리케이션 시작 시 메모리에 캐싱
- **메모리**: 메시지 파일 크기에 주의 (대량의 메시지는 별도 전략 필요)

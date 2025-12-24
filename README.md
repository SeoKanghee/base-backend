# Base Backend

- Make Application Great Again
- Spring Boot 4.0 기반의 백엔드 애플리케이션 템플릿입니다.
- **Gradle 멀티 모듈 프로젝트**와 **Spring Modulith**를 활용한 모듈화된 아키텍처를 제공합니다.

---

## 📋 목차

- [🛠 기술 스택](#🛠-기술-스택)
- [📁 프로젝트 구조](#📁-프로젝트-구조)
- [📦 모듈 및 애플리케이션](#📦-모듈-및-애플리케이션)
- [🚀 빠른 시작](#🚀-빠른-시작)
- [📖 문서 가이드](#📖-문서-가이드)
- [🔍 추가 정보](#🔍-추가-정보)

---

## 🛠 기술 스택

| 분류         | 기술                                                |
|------------|---------------------------------------------------|
| 언어 & 프레임워크 | Java 17, Spring Boot 4.0.0, Spring Modulith 2.0.0 |
| 빌드 도구      | Gradle Kotlin DSL (멀티 모듈)                         |
| 데이터베이스     | MariaDB, QueryDSL 5.1.0, Spring Data JPA          |
| 보안         | Spring Security, Jasypt 3.0.5, Native Vault       |
| 문서화        | SpringDoc OpenAPI 3.0.0                           |
| 테스트        | JUnit 5, JaCoCo                                   |

---

## 📁 프로젝트 구조

```
base-backend/
├── modules/                    # 재사용 가능한 라이브러리 모듈
│   ├── common/                 # 공통 모듈
│   ├── identity/               # 인증/인가 모듈
│   └── core/                   # 비즈니스 로직 모듈
│
├── apps/                       # 애플리케이션 그룹
│   ├── full/                   # 전체 모듈 포함 앱
│   └── identity/               # 인증 전용 앱
│
├── config/                     # 공유 설정
│   └── i18n/                   # 다국어 리소스
│
├── docs/                       # 문서 가이드
└── db-config/                  # DB 초기화 스크립트
```

---

## 📦 모듈 및 애플리케이션

### 라이브러리 모듈

| 모듈           | 설명                                  | 문서                                   |
|--------------|-------------------------------------|--------------------------------------|
| **Common**   | 감사 로깅, SSE, 암호화, i18n, 유틸리티 등 공통 기능 | [README](modules/common/README.md)   |
| **Identity** | 세션 기반 인증/인가, 계정 관리, 권한 체크 시스템       | [README](modules/identity/README.md) |
| **Core**     | 시스템 설정, 비즈니스 로직                     | [README](modules/core/README.md)     |

### 애플리케이션

| 애플리케이션           | 포함 모듈                    | 포트   | 문서                                |
|------------------|--------------------------|------|-----------------------------------|
| **app-full**     | Common + Identity + Core | 7904 | [README](apps/full/README.md)     |
| **app-identity** | Common + Identity        | 7479 | [README](apps/identity/README.md) |

---

## 🚀 빠른 시작

### 사전 요구사항

- Java 17 이상
- Gradle 9.2.1 이상
- MariaDB 10.x 이상

### 데이터베이스 설정

```bash
mysql -u root -p < db-config/00_created_db.sql
```

### 환경 변수 설정

프로젝트 루트 디렉토리에 `.env` 파일을 생성 혹은 수정하세요:

```properties
SPRING_PROFILES_ACTIVE=local
SERVER_PORT=7479
APPLICATION_NAME=base-backend
DATABASE_NAME=base_backend
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
COMMON_CRYPTO_KEY=your_crypto_key
```

### 빌드 및 실행

```bash
# 전체 컴파일
./gradlew compileJava

# app-full 실행
./gradlew :apps:app-full:bootRun

# app-identity 실행
./gradlew :apps:app-identity:bootRun
```

---

## 📖 문서 가이드

| 문서                                                                   | 설명                  |
|----------------------------------------------------------------------|---------------------|
| [AUDIT GUIDE](docs/AUDIT_GUIDE.md)                                   | 감사 로깅 시스템 사용 가이드    |
| [I18N USAGE GUIDE](docs/I18N_USAGE_GUIDE.md)                         | 다국어(i18n) 사용 가이드    |
| [MODULE COMMUNICATION GUIDE](docs/MODULE_COMMUNICATION_GUIDE.md)     | 모듈 간 통신 가이드         |
| [MULTI MODULE GUIDE](docs/MULTI_MODULE_GUIDE.md)                     | 멀티 모듈 프로젝트 가이드      |
| [PERMISSION USAGE GUIDE](docs/PERMISSION_USAGE_GUIDE.md)             | 권한 체크 시스템 사용 가이드    |
| [SPRING MODULITH GUIDE](docs/SPRING_MODULITH_GUIDE.md)               | Spring Modulith 가이드 |
| [SWAGGER IMPLEMENTATION GUIDE](docs/SWAGGER_IMPLEMENTATION_GUIDE.md) | 모듈별 Swagger 설정 가이드  |

---

## 🔍 추가 정보

### 새 모듈/애플리케이션 추가

새 모듈이나 애플리케이션을 추가하는 방법은 [MULTI MODULE GUIDE](docs/MULTI_MODULE_GUIDE.md)를 참조하세요.

### 문제 해결

**빌드 오류**

- Java 17 사용 여부 확인: `java -version`
- Gradle 캐시 정리: `./gradlew clean build --refresh-dependencies`

**프로젝트 구조 확인**

```bash
./gradlew projects
```

### 공통 영역 수정

- 공통 영역(modules/common)에 대한 추가 수정은 회의를 통해 결정합니다.

---

## 작성자

서강희

## 변경 이력

| 버전  | 날짜         | 변경 내역                                 |
|-----|------------|---------------------------------------|
| 1.0 | 2025-11-11 | 초안 작성                                 |
| 1.1 | 2025-12-17 | Spring Boot 4.0.0, Spring Modulith 적용 |
| 2.0 | 2025-12-18 | Gradle 멀티 모듈 프로젝트로 전환                 |
| 2.1 | 2025-12-19 | 문서 최신화                                |
| 2.2 | 2025-12-24 | 모듈별 README.md 분리                      |

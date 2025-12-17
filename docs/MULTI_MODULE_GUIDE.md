# 멀티 모듈 프로젝트 가이드

## 프로젝트 구조

```
base-backend/
├── .env                          # 모든 앱이 참조하는 설정 파일
├── config/
│   └── i18n/                     # 공유 i18n 리소스
│
├── modules/                      # 라이브러리 모듈 (공유 코드)
│   ├── common/                   # 공통 모듈 (유틸, 설정, 인터페이스 등)
│   ├── identity/                 # 인증/인가 모듈
│   └── core/                     # 비즈니스 로직 모듈
│
├── apps/                         # 애플리케이션 그룹
│   ├── full/                     # 애플리케이션: common + identity + core
│   │   ├── build.gradle.kts
│   │   └── src/
│   └── identity/                 # 애플리케이션: common + identity
│       ├── build.gradle.kts
│       └── src/
│
├── buildSrc/                     # 공통 빌드 로직
├── settings.gradle.kts           # 멀티 모듈 정의
├── build.gradle.kts              # 루트 빌드 설정
└── gradle/
    └── libs.versions.toml        # 버전 카탈로그
```

---

## 모듈 의존성 구조

```
modules/common       ← 기반 모듈 (외부 의존성만)
        ↑
modules/identity     ← common 의존
        ↑
modules/core         ← common, identity 의존
```

---

## 애플리케이션별 모듈 구성

| 애플리케이션 | Gradle 경로 | 포함 모듈 | 설명 |
|-------------|------------|-----------|------|
| app-full | `:apps:app-full` | common + identity + core | 전체 기능을 포함한 애플리케이션 |
| app-identity | `:apps:app-identity` | common + identity | 인증/인가 기능만 포함한 애플리케이션 |

---

## 빌드 명령어

### 전체 컴파일

```bash
./gradlew compileJava
```

### 특정 애플리케이션 빌드

```bash
# app-full JAR 빌드
./gradlew :apps:app-full:bootJar

# app-identity JAR 빌드
./gradlew :apps:app-identity:bootJar

# 모든 애플리케이션 빌드
./gradlew bootJar
```

### 빌드 결과물 위치

```
apps/full/build/libs/app-full.jar
apps/identity/build/libs/app-identity.jar
```

---

## 개발 환경 실행

```bash
# app-full 실행
./gradlew :apps:app-full:bootRun

# app-identity 실행
./gradlew :apps:app-identity:bootRun
```

**참고:** `bootRun` 실행 시 working directory가 각 애플리케이션 폴더(`apps/full/`, `apps/identity/`)로 설정됩니다.

---

## 프로덕션 배포 구조

```
{설치 루트}/
├── .env                          # 환경 설정 파일
├── config/
│   └── i18n/                     # 다국어 리소스
└── apps/
    ├── full/
    │   └── app-full.jar          # 실행 파일
    └── identity/
        └── app-identity.jar      # 실행 파일
```

### 실행 방법

```bash
cd {설치 루트}/apps/full
java -jar app-full.jar

# 또는
cd {설치 루트}/apps/identity
java -jar app-identity.jar
```

---

## 새 모듈 추가 방법

### 1. 모듈 디렉토리 생성

```bash
mkdir -p modules/license/src/main/java/com/kelly/base/license
```

### 2. build.gradle.kts 생성

```kotlin
// modules/license/build.gradle.kts
plugins {
    id("base-library-conventions")
}

dependencies {
    api(project(":modules:common"))
    // 필요한 의존성 추가
}
```

### 3. settings.gradle.kts에 모듈 추가

```kotlin
include(":modules:license")
```

### 4. 필요한 애플리케이션에서 의존성 추가

```kotlin
// apps/full/build.gradle.kts
dependencies {
    implementation(project(":modules:license"))
}
```

---

## 새 애플리케이션 추가 방법

### 1. 애플리케이션 디렉토리 생성

```bash
mkdir -p apps/license/src/main/java/com/kelly/base
mkdir -p apps/license/src/main/resources
```

### 2. build.gradle.kts 생성

```kotlin
// apps/license/build.gradle.kts
plugins {
    id("spring-app-conventions")
}

dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:license"))
    
    compileOnly(libs.lombok)
    runtimeOnly(libs.mariadb.java.client)
    annotationProcessor(libs.lombok)
}
```

### 3. Application 클래스 생성

```java
package com.kelly.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppLicenseApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppLicenseApplication.class, args);
    }
}
```

### 4. application.yml 생성

기존 애플리케이션의 `application.yml`을 복사하고 필요한 부분 수정.

**중요 경로 설정:**
```yaml
spring:
  config:
    import: optional:file:../../.env[.properties]

i18n:
  resource-path: file:../../config/i18n
```

### 5. settings.gradle.kts에 애플리케이션 추가

```kotlin
include(":apps:license")
project(":apps:license").name = "app-license"
```

---

## 참고 사항

1. **Convention Plugins**: `buildSrc/`에 정의된 convention plugins를 통해 빌드 설정을 재사용합니다.
   - `base-library-conventions`: 라이브러리 모듈용
   - `spring-app-conventions`: Spring Boot 애플리케이션용

2. **.env 파일 경로**: 모든 애플리케이션은 루트 폴더의 `.env` 파일을 참조합니다.
   - 경로: `../../.env`
   - 개발/프로덕션 환경 모두 동일한 상대 경로 사용

3. **config 폴더 경로**: i18n 리소스 등 설정 파일도 루트 폴더를 참조합니다.
   - 경로: `../../config/i18n`
   - 개발/프로덕션 환경 모두 동일한 상대 경로 사용

4. **버전 관리**: `gradle/libs.versions.toml`에서 의존성 버전을 중앙 관리합니다.

5. **프로젝트 이름 설정**: `settings.gradle.kts`에서 프로젝트 이름을 명시적으로 설정하여 모듈 이름 충돌을 방지합니다.

---

## 작성자

서강희

## 변경 이력

| 버전 | 날짜 | 변경 내역 |
|-----|------|---------|
| 1.0 | 2025-12-18 | 멀티 모듈 구조 초안 작성 |

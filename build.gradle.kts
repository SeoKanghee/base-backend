plugins {
    java
    jacoco
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.openapi.gradle.plugin)
    alias(libs.plugins.maven.settings)
}

group = "com.kelly.base"
version = "0.0.1"
description = "Base Project from Common Backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    maven {
        url = uri("https://maven.dcm4che.org")
    }
    maven {
        url = uri("https://raw.github.com/nroduit/mvn-repo/master/")
    }
}

val annotationProcessor by configurations
val compileOnly by configurations

compileOnly.extendsFrom(annotationProcessor)

dependencies {
    val classifierJakarta = "jakarta"

    // spring
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)

    // actuator
    implementation(libs.spring.boot.starter.actuator)

    // DB
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(variantOf(libs.querydsl.jpa) { classifier(classifierJakarta) })

    // clear vulnerabilities
    implementation(libs.bundles.clear.vulnerabilities)

    // swaager
    implementation(libs.springdoc)

    compileOnly(libs.lombok)

    runtimeOnly(libs.mariadb.java.client)

    annotationProcessor(libs.lombok)
    annotationProcessor(libs.spring.boot.configuration.processor)
    annotationProcessor(variantOf(libs.querydsl.apt) { classifier(classifierJakarta) })
    annotationProcessor(libs.annotation.api)
    annotationProcessor(libs.persistence.api)

    testImplementation(libs.spring.boot.starter.test)

    testCompileOnly(libs.lombok)

    testRuntimeOnly(libs.h2)
    testRuntimeOnly(libs.junit.platform.launcher)

    testAnnotationProcessor(libs.lombok)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.14"
}

// 커버리지 예외 처리를 위한 선언
val jacocoExcludes = listOf(
        "**/Q*.class",  // querydsl 에 의한 생성 파일
        "**/NativeVault*.class" // static 에서 jacoco 테스트 커버리지 정상 체크 안되는 문제 있음
)

tasks.jacocoTestReport {
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
    // jacocoExcludes 에 선언된 파일들을 test report 에서 제외
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoExcludes)
        }
    )
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
        }
    }
    // querydsl - Q class 를 coverage 에서 제외
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoExcludes)
        }
    )
}

tasks.build {
    dependsOn(tasks.jacocoTestReport)   // build 와 jacoco 연계
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
tasks.bootJar {
    enabled = true
    archiveFileName.set("${project.name}.jar")
}

tasks.jar {
    enabled = false
}

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(10, "minutes")
}

tasks.withType<Test> {
    maxHeapSize = "2048m"
    useJUnitPlatform()
}

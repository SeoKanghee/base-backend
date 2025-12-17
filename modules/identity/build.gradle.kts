plugins {
    `java-library`
    alias(libs.plugins.spring.dependency)
}

dependencyManagement {
    imports {
        mavenBom(libs.spring.boot.bom.get().toString())
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val classifierJakarta = "jakarta"

dependencies {
    api(project(":modules:common"))

    // spring security
    api(libs.spring.boot.starter.security)

    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)
    annotationProcessor(libs.spring.boot.configuration.processor)
    annotationProcessor(variantOf(libs.querydsl.apt) { classifier(classifierJakarta) })
    annotationProcessor(libs.annotation.api)
    annotationProcessor(libs.persistence.api)

    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.starter.security.test)
    testImplementation(libs.spring.modulith.starter.test)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testRuntimeOnly(libs.h2)
    testRuntimeOnly(libs.junit.platform.launcher)
}

// 커버리지 예외 처리를 위한 선언
val jacocoExcludes = listOf(
        "**/Q*.class", "**/internal/config/*Config*.class"  // querydsl 에 의한 생성 파일 및 설정 파일 제외
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

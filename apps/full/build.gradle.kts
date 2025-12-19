plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("${project.name}.jar")
}

tasks.jar {
    enabled = false
}

tasks.bootRun {
    workingDir = projectDir
}

dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:identity"))
    implementation(project(":modules:core"))

    compileOnly(libs.lombok)

    runtimeOnly(libs.mariadb.java.client)

    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.modulith.starter.test)
    testImplementation(libs.spring.modulith.docs)

    testRuntimeOnly(libs.junit.platform.launcher)
}

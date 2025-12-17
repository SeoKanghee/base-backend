fun loadEnvFile(): Map<String, String> {
    val envFile = file(".env")
    if (!envFile.exists()) {
        println(".env file not found, using defaults")
        return emptyMap()
    }

    return envFile.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
        .associate { line ->
            val parts = line.split("=", limit = 2)
            parts[0].trim() to parts[1].trim()
        }
}

val env = loadEnvFile()

plugins {
    java
    jacoco
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.maven.settings)
}

allprojects {
    group = env["APPLICATION_GROUP"] ?: "com.kelly.base"
    version = env["APPLICATION_VERSION"] ?: "0.0.1"

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.dcm4che.org") }
        maven { url = uri("https://raw.github.com/nroduit/mvn-repo/master/") }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    jacoco {
        toolVersion = "0.8.14"
    }

    tasks.withType<Test> {
        maxHeapSize = "2048m"
        useJUnitPlatform()
    }

    tasks.processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    configurations.all {
        resolutionStrategy.cacheDynamicVersionsFor(10, "minutes")
    }
}

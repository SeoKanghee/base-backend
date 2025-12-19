// .env 파일 로드
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

rootProject.name = env["APPLICATION_NAME"] ?: "base-backend"

// Enable version catalog
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Library modules
include(":modules:common")
include(":modules:identity")
include(":modules:core")

// Application modules
include(":apps:full")
project(":apps:full").name = "app-full"

include(":apps:identity")
project(":apps:identity").name = "app-identity"

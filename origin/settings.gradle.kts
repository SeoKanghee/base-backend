// .env 파일 로드
fun loadEnvFile(): Map<String, String> {
    val envFile = file("../.env")
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

// APPLICATION_NAME
rootProject.name = env["APPLICATION_NAME"] ?: "base-backend"
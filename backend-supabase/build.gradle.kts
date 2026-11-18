import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.lokahe.backend.supabase"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        // Backend endpoint configuration lives with the backend implementation
        // (same env/local.properties scheme as :app).
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        val spbUrlRaw = System.getenv("SPB_URL") ?:
            properties.getProperty("SPB_URL") ?: ""
        // Issue #3: an empty SPB_URL used to crash Retrofit.baseUrl() at startup.
        // Fall back to a reserved-TLD placeholder so the app boots and fails fast
        // (with an actionable message) on the first network call instead.
        val spbUrl = if (spbUrlRaw.isBlank()) {
            logger.warn(
                "SPB_URL is not configured (env/local.properties). Using placeholder " +
                        "'https://supabase-not-configured.invalid/' - requests will fail fast until it is set (issue #3)."
            )
            "https://supabase-not-configured.invalid/"
        } else {
            // Retrofit requires a base URL that ends with '/'
            if (spbUrlRaw.endsWith("/")) spbUrlRaw else "$spbUrlRaw/"
        }
        val anonKey = System.getenv("ANON_KEY") ?:
            properties.getProperty("ANON_KEY") ?: ""
        if (anonKey.isBlank()) {
            logger.warn("ANON_KEY is not configured (env/local.properties). Backend requests will return 401 until it is set.")
        }
        buildConfigField("String", "SPB_URL", "\"$spbUrl\"")
        buildConfigField("String", "ANON_KEY", "\"$anonKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    // Domain contracts + HTTP infrastructure (Retrofit/Gson exposed via :http api())
    api(project(":domain"))
    api(project(":http"))

    implementation(libs.kotlinx.coroutines.core)

    // Unified logging (:log)
    implementation(project(":log"))

    // Hilt (SupabaseRepository / ApiService / HttpConfig bindings)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
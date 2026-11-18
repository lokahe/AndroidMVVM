import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.lokahe.backend.rovortex"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        val rovortexUrlRaw = System.getenv("ROVORTEX_URL") ?:
            properties.getProperty("ROVORTEX_URL") ?: ""
        // Same fail-fast scheme as :backend-supabase (issue #3): an empty base
        // URL would crash Retrofit.baseUrl() at startup.
        val rovortexUrl = if (rovortexUrlRaw.isBlank()) {
            logger.warn(
                "ROVORTEX_URL is not configured (env/local.properties). Using placeholder " +
                        "'https://rovortex-not-configured.invalid/' - requests will fail fast until it is set."
            )
            "https://rovortex-not-configured.invalid/"
        } else {
            if (rovortexUrlRaw.endsWith("/")) rovortexUrlRaw else "$rovortexUrlRaw/"
        }
        buildConfigField("String", "ROVORTEX_URL", "\"$rovortexUrl\"")
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

    implementation(project(":log"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}

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
        val spbUrl = System.getenv("SPB_URL") ?:
            properties.getProperty("SPB_URL") ?: ""
        val anonKey = System.getenv("ANON_KEY") ?:
            properties.getProperty("ANON_KEY") ?: ""
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
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialzation)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.lokahe.auth"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        // Platform provider configuration is owned by :auth so host apps stay
        // free of provider wiring (same env/local.properties scheme as :app).
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        val googleClientId = System.getenv("GOOGLE_WEB_CLIENT_ID") ?:
            properties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleClientId\"")
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
    implementation(libs.kotlinx.coroutines.core)
    // Session is a plain serializable value object (no Gson in this module)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)

    // Platform credential providers (Credential Manager + GoogleID fallback)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    // Unified logging (:log)
    implementation(project(":log"))

    // Hilt (TokenStore / PkceStore / AuthProvider bindings)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
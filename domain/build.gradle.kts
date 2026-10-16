plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialzation)
}

android {
    namespace = "com.lokahe.domain"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
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
    // Repository contracts reference ApiResult (:http) and Session (:auth);
    // api() so consumers (app, backend modules) see them transitively.
    api(project(":http"))
    api(project(":auth"))

    implementation(libs.kotlinx.coroutines.core)
    // Domain Post is @Serializable for Navigation 3 route passing
    implementation(libs.kotlinx.serialization.json)
}
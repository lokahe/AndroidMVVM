plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.lokahe.store"
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
    // Preferences DataStore (typed key-value persistence)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.coroutines.core)

    // Hilt (PreferencesModule bindings; @ApplicationContext qualifier)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
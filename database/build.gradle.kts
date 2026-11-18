plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.lokahe.database"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        // DataBaseModule reads BuildConfig.DEBUG for query logging
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
    // Room (entities, DAOs, database + suspend support)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.coroutines.core)

    // Unified logging (query callback)
    implementation(project(":log"))

    // Hilt (DataBaseModule bindings)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
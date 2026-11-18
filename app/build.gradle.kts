import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialzation)
}

android {
    namespace = "com.lokahe.androidmvvm"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lokahe.androidmvvm"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        // Backend URL for :backend-rovortex (env for CI, local.properties for dev).
        val rawRovortexUrl = (System.getenv("ROVORTEX_URL")
            ?: properties.getProperty("ROVORTEX_URL") ?: "").trim()

        // Retrofit requires an absolute http(s) URL that ends with '/'.
        val rovortexUrl = when {
            rawRovortexUrl.isEmpty() -> {
                logger.warn(
                    "ROVORTEX_URL is not set (local.properties / env). " +
                        "Using a placeholder so the app can start; network calls will fail gracefully."
                )
                "https://rovortex-not-configured.invalid/"
            }
            !rawRovortexUrl.endsWith("/") -> {
                logger.warn("ROVORTEX_URL does not end with '/' — appended automatically for Retrofit baseUrl.")
                "$rawRovortexUrl/"
            }
            else -> rawRovortexUrl
        }

        buildConfigField("String", "ROVORTEX_URL", "\"$rovortexUrl\"")
    }

    signingConfigs {
        create("debugSigningConfig") {
            storeFile = file("debug.keystore")
            storePassword = "android"          // Added '=' for consistency
            keyAlias = "androiddebugkey"       // Added '=' for consistency
            keyPassword = "android"              // Added '=' for consistency
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // 定義した署名設定を適用
            signingConfig = signingConfigs.getByName("debugSigningConfig")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material)
    implementation(libs.coil.compose)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.material.icons.extended)
    //navigation3
    implementation("androidx.navigation3:navigation3-runtime:1.1.0-alpha04")
    implementation("androidx.navigation3:navigation3-ui:1.1.0-alpha04")
    implementation(libs.kotlinx.serialization.json)
//    implementation(libs.androidx.browser)

    // Room database moved to :database (entities/DAOs/DataBaseRepository)
    //hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Common HTTP module (OkHttp / Retrofit / ApiResult / safeApiCall)
    implementation(project(":http"))

    // Common log module (L / LogConfig / LogSink)
    implementation(project(":log"))

    // Backend-neutral auth kit (Session / TokenStore / PKCE / AuthProvider)
    implementation(project(":auth"))

    // Domain contracts + models (repository interfaces, pure domain models)
    implementation(project(":domain"))

    // Rovortex implementation of the domain contracts
    implementation(project(":backend-rovortex"))

    // Preferences DataStore infrastructure (stores / PreferencesRepository)
    implementation(project(":store"))

    // Local Room database (entities / DAOs / DataBaseRepository)
    implementation(project(":database"))

    // Retrofit / OkHttp / Gson now come transitively from :http (:backend-supabase owns the API)
    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Backendless
//    implementation(libs.backendless)

    // Platform credential providers now live in :auth (GoogleAuthProvider)

//Firebase
//    implementation(platform("com.google.firebase:firebase-bom:33.8.0")) // Note: 33.8.0 is the current latest stable, 34.8.0 doesn't exist yet
//    implementation("com.google.firebase:firebase-auth")

}
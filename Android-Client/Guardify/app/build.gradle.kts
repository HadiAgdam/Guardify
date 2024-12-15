plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

android {
    namespace = "ir.the_code.guardify"
    compileSdk = 35

    defaultConfig {
        applicationId = "ir.the_code.guardify"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation(libs.kotlinx.serialization.json)

    // Haze
    implementation(libs.haze)
    implementation(libs.haze.materials)


    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.androidx.workmanager)


    // Navigation
    implementation(libs.androidx.navigation.compose)


    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Material Motions
    implementation(libs.material.motion.compose.core)

    // Coil
    implementation(libs.coil.compose)

    // Haze
    implementation(libs.haze.v110)
    implementation(libs.haze.materials.v110)

    // Web view
    implementation(libs.accompanist.webview)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)


    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Biometric
    implementation(libs.androidx.biometric)


    // Lottie
    implementation(libs.lottie.compose)
}

android {
    lint {
        baseline = file("lint-baseline.xml")
    }
}

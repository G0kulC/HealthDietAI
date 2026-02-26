plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    id("kotlin-parcelize")
}

android {
    namespace = "com.healthdietapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.healthdietapp"
        minSdk = 26
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    // Coroutines
    implementation(libs.coroutines.android)
    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.runtime)
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    // Shimmer
    implementation(libs.shimmer)
    // SwipeRefresh
    implementation(libs.swiperefresh)
    // Glide
    implementation(libs.glide)
    // MPAndroidChart
    implementation(libs.mpandroidchart)
}

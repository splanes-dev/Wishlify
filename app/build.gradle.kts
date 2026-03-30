plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.compose)
  alias(libs.plugins.jetbrains.kotlin.serialization)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.splanes.uoc.wishlify"
  compileSdk {
    version = release(36)
  }

  defaultConfig {
    applicationId = "com.splanes.uoc.wishlify"
    minSdk = 30
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

dependencies {
  // Projects
  implementation(projects.data)
  implementation(projects.domain)
  implementation(projects.presentation)

  // Libs
  // Android
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.bundles.lifecycle)
  implementation(libs.bundles.compose)
  // Firebase analytics
  implementation(platform(libs.google.firebase.bom))
  implementation(libs.google.firebase.analytics)
  // Koin
  implementation(libs.koin.android)
  implementation(libs.koin.android.compose)
  // Logger
  implementation(libs.timber)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}
plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.compose)
  alias(libs.plugins.jetbrains.kotlin.serialization)
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
  }
}

dependencies {
  // Libs
  implementation(libs.androidx.core.ktx)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.bundles.lifecycle)
  implementation(libs.bundles.compose)
  implementation(libs.material)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}
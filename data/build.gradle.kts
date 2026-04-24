plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
  namespace = "com.splanes.uoc.wishlify.data"
  compileSdk {
    version = release(36)
  }

  defaultConfig {
    minSdk = 30

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
  // Projects
  implementation(projects.domain)
  // Libs
  // Serialization
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.kotlinx.serialization.json)
  // Koin
  implementation(libs.koin.android)
  // Social Login
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.playServicesAuth)
  implementation(libs.google.identity)
  // Firebase
  implementation(platform(libs.google.firebase.bom))
  implementation(libs.google.firebase.auth)
  implementation(libs.google.firebase.firestore)
  implementation(libs.google.firebase.storage)
  implementation(libs.google.firebase.functions)
  implementation(libs.google.firebase.messaging)
  // Timber
  implementation(libs.timber)
}
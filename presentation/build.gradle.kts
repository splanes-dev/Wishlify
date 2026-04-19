plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.jetbrains.kotlin.compose)
  alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
  namespace = "com.splanes.uoc.wishlify.presentation"
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
  buildFeatures {
    compose = true
  }
}

dependencies {
  // Projects
  implementation(projects.domain)

  // Libs
  // Kotlin
  implementation(libs.kotlinx.serialization.json)
  // Android
  implementation(libs.androidx.core.ktx)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.bundles.lifecycle)
  implementation(libs.bundles.compose)
  // Social Login
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.playServicesAuth)
  implementation(libs.google.identity)
  // Koin
  implementation(libs.koin.android)
  implementation(libs.koin.android.compose)
  // Coil
  implementation(libs.androidx.compose.coil)
  // Logger
  implementation(libs.timber)

  // Testing (unit test)
  testImplementation(libs.junit)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.mockito)
  testImplementation(libs.truth)
  testImplementation(libs.turbine)
  testImplementation(libs.coroutines.test)
}
plugins {
  alias(libs.plugins.android.library)
}

android {
  namespace = "com.splanes.uoc.wishlify.domain"
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
  // Libs
  implementation(libs.koin.android)
  implementation(libs.timber)
}
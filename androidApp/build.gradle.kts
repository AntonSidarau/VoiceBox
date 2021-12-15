plugins {
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.fragment:fragment:1.3.6")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.1")
    implementation("dev.chrisbanes.insetter:insetter:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
}

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "com.jovvi.voicebox.android"
        minSdk = 24
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

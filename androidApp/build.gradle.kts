plugins {
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.fragment:fragment:1.4.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.1")
    implementation("dev.chrisbanes.insetter:insetter:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.jovvi.voicebox.android"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

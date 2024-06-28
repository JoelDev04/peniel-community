plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "id.jostudios.penielcommunity"
    compileSdk = 34

    defaultConfig {
        applicationId = "id.jostudios.penielcommunity"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage:20.0.3")
    implementation("com.google.firebase:firebase-auth")

    implementation("com.google.code.gson:gson:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation("androidx.fragment:fragment-ktx:1.8.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    androidTestImplementation(libs.junit.junit)

}
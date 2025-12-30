plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    id("app.cash.sqldelight")
    alias(libs.plugins.google.services)
}
sqldelight {
    databases {
        create("JobTrackerDatabase") {
            packageName.set("com.jobtracker.shared.database")
            generateAsync.set(false)
        }
    }
}
android {
    namespace = "com.jobtracker.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jobtracker.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api" // Add this line
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)

}

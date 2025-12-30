import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)

    // ✅ required for cocoapods { } in KMP
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget {
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // ✅ this is what links Xcode <-> shared module
    cocoapods {
        summary = "Shared code for Job Tracker App"
        homepage = "https://github.com/jobtracker"
        version = "1.0"
        ios.deploymentTarget = "14.0"

        framework {
            baseName = "shared"
            isStatic = true
        }

        // iOS Firebase via CocoaPods
        pod("FirebaseAuth")
        pod("FirebaseFirestore")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)
                implementation(libs.sqldelight.coroutines.extensions)
            }
        }

        val commonTest by getting

        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.sqldelight.android.driver)
                implementation(libs.sqldelight.sqlite.driver)
                implementation(libs.koin.android)

                // ✅ Android Firebase via Gradle (with explicit versions)
                implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
                implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
            }
        }

        // ✅ create shared iOS source set and connect it
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.sqldelight.native.driver)
            }
        }

        val iosTest by creating {
            dependsOn(commonTest)
        }

        // attach to each target
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

        val iosX64Test by getting { dependsOn(iosTest) }
        val iosArm64Test by getting { dependsOn(iosTest) }
        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }
    }
}

android {
    namespace = "com.jobtracker.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    databases {
        create("JobTrackerDatabase") {
            packageName.set("com.jobtracker.shared.database")
            generateAsync.set(false)
        }
    }
}

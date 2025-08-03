plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mj.poker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mj.poker"
        minSdk = 24
        targetSdk = 35
        versionCode = 101
        versionName = "1.1"
    }
    useLibrary("org.apache.http.legacy")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    //Libraries
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))
    implementation(libs.fastjson)
    implementation(libs.bottom.navigation.bar)
    implementation(libs.lsettingviewlibrary)
    implementation(libs.circleimageview)
    implementation(libs.xpopup)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.dnsjava)
    implementation(libs.commons.lang)

    //Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)


    //35:
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.runtime)
}
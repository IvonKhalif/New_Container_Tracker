plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinParcelize)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.containertracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.containertracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 2424
        versionName = "2.4.1"

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
    buildFeatures {
        dataBinding = true
//        viewBinding = true
        buildConfig = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    this.buildOutputs.all {
        val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        val outputFileName = "Container Tracker.apk"
        variantOutputImpl.outputFileName = outputFileName
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(files("libs/printersdkv1.jar"))
    implementation(libs.multidex)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    implementation(libs.constraintlayout)
    implementation(libs.kotlinParcelize)
    implementation(libs.androidx.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.glide)
    implementation(libs.glide.okhttp)
    implementation(libs.shimmer)
    implementation(libs.lottie)
    implementation(libs.zxing)
    implementation(libs.coroutines)
    implementation(libs.viewModel)
    implementation(libs.liveData)
    implementation(libs.runtime)
    implementation(libs.commonJava)
    implementation(libs.lifecycle.service)
    implementation(libs.lifecycle.process)
    implementation(libs.viewmodel.savedstate)
    implementation(libs.lifecycle.extensions)
    implementation(libs.koin.android)
//    implementation(libs.koin.viewmodel)
//    implementation(libs.koin.core)

    implementation(libs.rxbinding)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.networkResponseAdapter)

    implementation(libs.threetenabp)
    implementation(libs.compressor)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.extensions)
    implementation(libs.camera.view)
    implementation(libs.zoomy)

//    debugImplementation(libs.chucker.debug)
//    releaseImplementation(libs.chucker.release)
}
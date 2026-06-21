plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle.plugin)
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}
android {
    namespace = "com.example.skynet"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.skynet"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // CAMBIO AQUÍ: Configuración nativa del compilador de Kotlin


    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.cardview)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.coil.compose)
    
    // Splash Screen
    implementation(libs.core.splashscreen)
    
    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Google Play Services
    implementation(libs.gms.maps)
    implementation(libs.gms.base)
    implementation(libs.gms.auth)
    implementation(libs.gms.location)
    
    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    // UI Libraries
    implementation(libs.glide)
    implementation(libs.glide.okhttp)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.lottie)
    implementation(libs.sliding.root.nav)
    
    implementation(libs.gridlayout)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    implementation(libs.mpandroidchart)

    // QR Code Generation
    implementation(libs.zxing.android)
    implementation(libs.zxing.core)

    // RxJava
    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    // Video Player
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
    
    // STOMP WebSocket
    implementation(libs.stomp)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // ML Kit Face Detection
    implementation(libs.mlkit.face.detection) {
        exclude(group = "com.google.ai.edge.litert")
    }

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support) {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support-api")
    }

    // PhotoView para Tour Virtual / Zoom en fotos
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

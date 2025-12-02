plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.apphuertohogar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.apphuertohogar"
        minSdk = 24
        targetSdk = 36
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

    // ✅ ESTO ESTÁ PERFECTO AHORA
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- LIBRERÍAS DE RED Y DATOS ---
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.datastore.preferences)
    // implementation(libs.androidx.datastore.preferences.v120) // Duplicada, dejé la estándar

    // --- IMÁGENES ---
    implementation(libs.coil.compose)

    // --- SEGURIDAD (Si aún la usas, sino puedes borrarla) ---
    implementation(libs.jbcrypt)

    // --- CORRUTINAS ---
    implementation(libs.kotlinx.coroutines.android)

    // --- ROOM (BASE DE DATOS LOCAL) ---
    val room_version = "2.6.1"
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- ANDROID & COMPOSE UI ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.material.icons.extended.android)

    // --- TESTING UNITARIO (JUNIT 5 & KOTEST) ---
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit) // JUnit 4 Legacy
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // --- TESTING INSTRUMENTADO (UI) ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4) // Compose UI Test
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
} // <--- ¡ESTA LLAVE CIERRA LAS DEPENDENCIAS!

// ✅ ESTO DEBE IR AFUERA DE "dependencies"
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.sensazionapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sensazionapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["auth0Domain"] = "dev-vguxq7gicpeheej8.us.auth0.com"
        manifestPlaceholders["auth0Scheme"] = "demo"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

        //Auth0
    implementation(libs.auth0)
    implementation("com.google.code.gson:gson:2.12.1")

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.browser)

    //Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.compose.v277)
    implementation(libs.androidx.runtime.livedata)

    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("com.google.android.gms:play-services-basement:18.0.0")
    implementation ("com.google.android.gms:play-services-auth:20.2.0")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.google.android.gms:play-services-base:18.2.0")

    implementation("com.google.android.gms:play-services-auth:20.1.0")
    //implementation("com.google.firebase:firebase-auth-ktx")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.animation.core.lint)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
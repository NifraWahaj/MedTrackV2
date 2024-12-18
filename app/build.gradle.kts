plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.medtrack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medtrack"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.google.firebase:firebase-bom:33.5.1")
    implementation ("com.google.firebase:firebase-auth:23.1.0")
    implementation ("com.google.firebase:firebase-database:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.itextpdf:itext7-core:7.1.15") // For iText PDF library
    implementation("com.itextpdf:font-asian:7.1.16") // for fonts and characters support

    // Retrofit dependency for networking
    implementation ("com.squareup.retrofit2:retrofit:2.9.0") // Use the latest stable version
    implementation("androidx.palette:palette:1.0.0")



    // Gson dependency for JSON serialization/deserialization
    implementation ("com.google.code.gson:gson:2.6.2") // Use the version you prefer or the latest
    implementation ("androidx.cardview:cardview:1.0.0")
// image retrieval in Firebase
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // rich text editor API
    implementation( "jp.wasabeef:richeditor-android:2.0.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("androidx.work:work-runtime:2.8.1")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("androidx.work:work-runtime-ktx:2.8.1")



}




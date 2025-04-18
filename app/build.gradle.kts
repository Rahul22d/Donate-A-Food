import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.firebase.perf)

}

// 🔐 Load API keys from secrets.properties or fallback
val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        load(secretsFile.inputStream())
    }
}

val defaultProperties = Properties().apply {
    val defaultFile = rootProject.file("local.defaults.properties")
    if (defaultFile.exists()) {
        load(defaultFile.inputStream())
    }
}

val mapsApiKey = secretsProperties.getProperty("MAPS_API_KEY")
    ?: defaultProperties.getProperty("MAPS_API_KEY")
    ?: error("MAPS_API_KEY is missing from both secrets.properties and local.defaults.properties")


android {
    namespace = "com.rahul.donate_a_food"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rahul.donate_a_food"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🔑 Inject the Google Maps API key into R.string.google_maps_key
        resValue("string", "google_maps_key", mapsApiKey)
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
    buildFeatures{
        viewBinding = true
    }



}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
//    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.location)
    implementation(libs.firebase.perf)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // add some
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation (libs.firebase.bom)
    implementation("com.google.firebase:firebase-appcheck-safetynet:16.1.2")
    implementation ("com.google.firebase:firebase-appcheck")
    implementation ("com.android.support:multidex-instrumentation:1.0.3")
    //circle image view
//    implementation (libs.circleimageview)
    implementation (libs.play.services.maps)
//    implementation (libs.geofire.android)
//    implementation (libs.geofire.android.common)
    // for geohash
//    implementation (libs.geohash.java.v5)
//    implementation (libs.geohash.java)
//    implementation (libs.appcompat)
//    implementation (libs.geofire.java)
    // Add this to your app/build.gradle
//    implementation (libs.geofire.android.common.v310)
    implementation ("androidx.work:work-runtime:2.10.0")
    implementation (libs.guava)
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation (libs.osmdroid.osmdroid.android.v6111)
    implementation (libs.play.services.location.v2101)
    implementation (libs.osmdroid.android.v6116)
    // For FCM token
    implementation (libs.firebase.messaging)

    implementation (libs.play.services.maps.v1820)

    implementation (libs.volley)
//    implementation("com.google.firebase:firebase-analytics")
//    implementation (libs.firebase.admin)

    // Import the BoM for the Firebase platform
    implementation(libs.firebase.bom.v33110)
    // FirebaseUI for Firebase Auth
    implementation (libs.firebase.auth.v2212)  // Latest Firebase Auth
    implementation (libs.firebase.ui.auth.v802)     // FirebaseUI Auth

//    implementation (libs.firebase.appcheck.playintegrity)
//    implementation (libs.firebase.appcheck.playintegrity.v1701)
    implementation (libs.material)

}


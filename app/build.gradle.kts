plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}

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
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // add some
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation (platform("com.google.firebase:firebase-bom:33.8.0"))
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
}
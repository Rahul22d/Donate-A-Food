// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.firebase.perf) apply false

}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add the Firebase plugin classpath
        classpath ("com.google.gms:google-services:4.4.2") // make sure this is the latest version
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("com.google.secrets_gradle_plugin") version "0.4"
}
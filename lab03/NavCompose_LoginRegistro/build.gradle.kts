// build.gradle.kts (Module :app)
dependencies {
    implementation("androidx.navigation:navigation-compose:2.8.0")
// ...el resto de dependencias que ya genera la plantilla
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
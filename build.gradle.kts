import java.util.Properties

val localPropertiesFile = rootProject.file("local.properties")
var sdkDir: String? = null

if (localPropertiesFile.exists()) {
    val properties = Properties()
    localPropertiesFile.inputStream().use { properties.load(it) }
    sdkDir = properties.getProperty("sdk.dir")
}

if (sdkDir.isNullOrEmpty()) {
    sdkDir = System.getenv("ANDROID_SDK_ROOT")
}

if (sdkDir.isNullOrEmpty()) {
    throw GradleException(
        "SDK location not found. Please set ANDROID_SDK_ROOT or create local.properties with sdk.dir."
    )
}

// Set ke gradle agar dikenali Android plugin
System.setProperty("android.home", sdkDir)

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
}
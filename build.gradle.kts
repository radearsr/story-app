val localPropertiesFile = rootProject.file("local.properties")

// Cek apakah file sudah ada
if (!localPropertiesFile.exists()) {
    val sdkDir = System.getenv("ANDROID_SDK_ROOT") ?: System.getenv("ANDROID_HOME")

    if (sdkDir != null && sdkDir.isNotEmpty()) {
        println("👉 [Gradle] Creating local.properties with sdk.dir=$sdkDir")
        localPropertiesFile.writeText("sdk.dir=$sdkDir\n")
    } else {
        throw GradleException(
            "❌ SDK location not found. Please set ANDROID_SDK_ROOT or create local.properties with sdk.dir."
        )
    }
}

// Plugin management
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
}

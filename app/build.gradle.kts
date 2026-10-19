plugins { id("com.android.application") }
android {
    namespace = "com.face.studio.ko"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.face.studio.ko"
        minSdk = 29
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"
    }
    buildTypes { release { isMinifyEnabled = false } }
}

dependencies {
    implementation("com.google.mlkit:segmentation-selfie:16.0.0-beta6")
}

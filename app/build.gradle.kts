plugins { id("com.android.application") }
android {
    namespace = "com.face.studio.ko"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.face.studio.ko"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes { release { isMinifyEnabled = false } }
}

plugins {
    id("com.laurentvrevin.android.library")
}

android {
    namespace = "com.laurentvrevin.wheris.core.datastore"
}

dependencies {
    implementation(libs.datastore.preferences)
}

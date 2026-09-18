plugins {
    id("com.laurentvrevin.android.library")
}

android {
    namespace = "com.laurentvrevin.wheris.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}

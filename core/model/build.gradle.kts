plugins {
    id("com.laurentvrevin.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.laurentvrevin.wheris.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

plugins {
    id("com.laurentvrevin.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.laurentvrevin.androidstarter.core"
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.compose.ui)
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

plugins {
    id("com.laurentvrevin.android.library.compose")
}

android {
    namespace = "com.laurentvrevin.wheris.core.designsystem"
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
}

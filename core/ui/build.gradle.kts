plugins {
    id("com.laurentvrevin.android.library.compose")
}

android {
    namespace = "com.laurentvrevin.wheris.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

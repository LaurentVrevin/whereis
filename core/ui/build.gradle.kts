plugins {
    id("com.laurentvrevin.android.library.compose")
}

android {
    namespace = "com.laurentvrevin.wheris.core.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(libs.compose.foundation)

    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
}

plugins {
    id("com.laurentvrevin.android.library")
}

android {
    namespace = "com.laurentvrevin.wheris.core.navigation"
}

dependencies {
    implementation(project(":core:model"))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

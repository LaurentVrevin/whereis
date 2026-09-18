plugins {
    id("com.laurentvrevin.android.library")
}

android {
    namespace = "com.laurentvrevin.wheris.core.map"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":domain"))
}

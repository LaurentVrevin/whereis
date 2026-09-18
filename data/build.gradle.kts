plugins {
    id("com.laurentvrevin.android.library")
}

android {
    namespace = "com.laurentvrevin.wheris.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))

    implementation(libs.koin.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

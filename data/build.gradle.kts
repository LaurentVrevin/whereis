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
    implementation(project(":core:common"))
    implementation(project(":core:location"))
    implementation(project(":core:map"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    androidTestImplementation(libs.room.runtime)
    androidTestImplementation(project(":core:photo"))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

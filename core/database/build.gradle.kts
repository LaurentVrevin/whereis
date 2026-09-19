plugins {
    id("com.laurentvrevin.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.laurentvrevin.wheris.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    testImplementation(libs.room.testing)
}

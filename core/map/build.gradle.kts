import java.util.Properties

plugins {
    id("com.laurentvrevin.android.library.compose")
}

val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { input -> load(input) }
        }
    }

val mapboxPublicToken = localProperties.getProperty("MAPBOX_PUBLIC_TOKEN").orEmpty()

android {
    namespace = "com.laurentvrevin.wheris.core.map"

    defaultConfig {
        resValue("string", "mapbox_access_token", mapboxPublicToken)
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)

    implementation(libs.mapbox.maps)
    implementation(libs.mapbox.compose)

    testImplementation(libs.junit)
}

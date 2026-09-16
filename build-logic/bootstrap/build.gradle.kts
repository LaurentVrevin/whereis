plugins {
    `kotlin-dsl`
}

group = "com.laurentvrevin.bootstrap"

gradlePlugin {
    plugins {
        register("projectBootstrap") {
            id = "com.laurentvrevin.project.bootstrap"
            implementationClass = "com.laurentvrevin.bootstrap.BootstrapPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    testImplementation(libs.junit)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

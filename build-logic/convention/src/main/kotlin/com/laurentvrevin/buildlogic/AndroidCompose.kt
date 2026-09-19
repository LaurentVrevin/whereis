package com.laurentvrevin.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.buildFeatures {
        compose = true
    }

    val libs = extensions
        .getByType<VersionCatalogsExtension>()
        .named("libs")

    dependencies {
        val composeBom = libs.findLibrary("compose-bom").get()

        add("implementation", platform(composeBom))
        add("androidTestImplementation", platform(composeBom))
    }
}
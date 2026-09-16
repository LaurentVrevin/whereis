package com.laurentvrevin.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project

class BootstrapPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("bootstrapProject", BootstrapTask::class.java)
    }
}

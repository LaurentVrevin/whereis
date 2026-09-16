package com.laurentvrevin.bootstrap

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Properties
import java.util.Scanner

abstract class BootstrapTask : DefaultTask() {

    @TaskAction
    fun bootstrap() {
        val targetDirProperty = project.findProperty("targetDir")?.toString()
        val rootDir = if (targetDirProperty != null) File(targetDirProperty) else project.rootDir

        val starterPropertiesFile = File(rootDir, "starter.properties")
        val completionFile = File(rootDir, ".bootstrap-complete")

        if (completionFile.exists()) {
            throw GradleException("Project has already been bootstrapped.")
        }
        if (!starterPropertiesFile.exists()) {
            throw GradleException("starter.properties not found in ${rootDir.absolutePath}")
        }

        val starterProperties = Properties().apply {
            starterPropertiesFile.inputStream().use { load(it) }
        }

        val oldProjectName = starterProperties.getProperty("starter.projectName") ?: ""
        val oldDisplayName = starterProperties.getProperty("starter.displayName") ?: ""
        val oldPackageName = starterProperties.getProperty("starter.packageName") ?: ""
        val oldThemeName = starterProperties.getProperty("starter.themeName") ?: ""

        if (oldProjectName.isBlank() || oldDisplayName.isBlank() || oldPackageName.isBlank() || oldThemeName.isBlank()) {
            throw GradleException("Mandatory source properties in starter.properties are missing or empty.")
        }

        val scanner = Scanner(System.`in`)

        var projectNameInput = project.findProperty("projectName")?.toString()
        if (projectNameInput == null) {
            print("Enter Gradle Project Name (PascalCase, e.g. MonApplication): ")
            projectNameInput = scanner.nextLine()
        }

        var appDisplayNameInput = project.findProperty("appDisplayName")?.toString()
        if (appDisplayNameInput == null) {
            print("Enter Application Display Name (e.g. Mon application): ")
            appDisplayNameInput = scanner.nextLine()
        }

        var packageNameInput = project.findProperty("packageName")?.toString()
        if (packageNameInput == null) {
            print("Enter Android Package Name (e.g. com.exemple.monapplication): ")
            packageNameInput = scanner.nextLine()
        }

        val dryRun = project.findProperty("dryRun")?.toString() == "true"
        var confirm = project.findProperty("confirm")?.toString() == "true"

        if (projectNameInput.isNullOrBlank() || appDisplayNameInput.isNullOrBlank() || packageNameInput.isNullOrBlank()) {
            throw GradleException("Inputs cannot be empty.")
        }

        val projectName: String = projectNameInput.trim()
        val appDisplayName: String = appDisplayNameInput.trim()
        val packageName: String = packageNameInput.trim()

        val logic = BootstrapLogic(rootDir, dryRun)

        logic.validateProjectName(projectName)
        logic.validatePackageName(packageName, oldPackageName)
        
        // Conflict detection (runs in dry-run too)
        logic.checkConflicts(oldPackageName, packageName)

        val newThemeName = "Theme.$projectName"
        val escapedAppDisplayName = logic.escapeXml(appDisplayName)

        println("\n🚀 Bootstrapping project...")
        println("   Target Directory: ${rootDir.absolutePath}")
        println("   Project Name: $oldProjectName -> $projectName")
        println("   Display Name: $oldDisplayName -> $appDisplayName (XML: $escapedAppDisplayName)")
        println("   Package Name: $oldPackageName -> $packageName")
        println("   Theme Name:   $oldThemeName -> $newThemeName")
        if (dryRun) println("   [DRY RUN MODE - No changes will be applied]")

        if (!confirm && !dryRun) {
            print("\n⚠️  Confirm applying these changes? (y/N): ")
            val response = scanner.nextLine()
            confirm = response.trim().lowercase() == "y"
        }

        if (!confirm && !dryRun) {
            println("❌ Bootstrap cancelled by user.")
            return
        }

        // Precise transformations using Regex where possible
        val regexTransformations = listOf(
            Regex("rootProject\\.name\\s*=\\s*[\"']$oldProjectName[\"']") to "rootProject.name = \"$projectName\"",
            Regex("<string name=\"app_name\">$oldDisplayName</string>") to "<string name=\"app_name\">$escapedAppDisplayName</string>",
            Regex("Theme\\.$oldProjectName") to "Theme.$projectName"
        )

        val stringTransformations = listOf(
            oldPackageName to packageName,
            oldPackageName.replace(".", "/") to packageName.replace(".", "/"),
            oldProjectName to projectName // Fallback
        )

        logic.applyTransformations(stringTransformations, regexTransformations)
        logic.movePackageDirectories(oldPackageName, packageName)
        logic.renameRoomSchemas(oldPackageName, packageName)

        if (!dryRun) {
            if (!starterPropertiesFile.delete()) {
                throw GradleException("Failed to delete starter.properties. Please delete it manually and create .bootstrap-complete to finish.")
            }
            completionFile.writeText("Bootstrapped on ${java.time.LocalDateTime.now()}\n")
            println("✅ Project successfully bootstrapped!")
        }
    }
}

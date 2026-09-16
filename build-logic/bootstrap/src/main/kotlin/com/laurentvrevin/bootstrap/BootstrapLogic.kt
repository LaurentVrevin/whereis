package com.laurentvrevin.bootstrap

import java.io.File

class BootstrapLogic(
    private val rootDir: File,
    private val dryRun: Boolean = false,
) {
    private val allowedExtensions = setOf(
        "kt", "kts", "java", "xml", "properties", "toml", "md",
        "yml", "yaml", "json", "pro", "gitignore", "editorconfig"
    )

    private val excludedDirectories = setOf(
        "build", "out", ".git", ".gradle", ".idea", ".artifacts", ".kotlin"
    )

    fun validateProjectName(projectName: String) {
        if (projectName.isBlank()) {
            throw IllegalArgumentException("Project Name cannot be empty.")
        }
        val regex = Regex("^[A-Z][a-zA-Z0-9]*$")
        if (!regex.matches(projectName)) {
            throw IllegalArgumentException("Project Name must be PascalCase (e.g. MyProject) and contain no spaces or special characters.")
        }
    }

    fun validatePackageName(packageName: String, sourcePackage: String) {
        val segments = packageName.split(".")
        if (segments.size < 2) {
            throw IllegalArgumentException("Package name must have at least two segments.")
        }
        val regex = Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$")
        if (!regex.matches(packageName)) {
            throw IllegalArgumentException("Invalid package name format (e.g. com.my.app).")
        }
        if (packageName == sourcePackage) {
            throw IllegalArgumentException("Target package name must be different from source package.")
        }
        if (packageName == "com.example" || packageName.startsWith("com.example.")) {
            throw IllegalArgumentException("Package name cannot be com.example.")
        }
    }

    fun checkConflicts(oldPackage: String, newPackage: String) {
        val srcDirs = getSourceFolders()
        val oldPathSuffix = oldPackage.replace(".", File.separator)
        val newPath = newPackage.replace(".", File.separator)

        // Source directories conflicts
        rootDir.walkTopDown()
            .onEnter { it.name !in excludedDirectories }
            .filter { it.isDirectory && it.path.endsWith(oldPathSuffix) }
            .filter { dir -> isInsideSourceFolder(dir, srcDirs) }
            .forEach { dir ->
                val rootSrcDir = findRootSrcDir(dir, srcDirs)
                if (rootSrcDir != null) {
                    val targetDir = File(rootSrcDir, newPath)
                    if (targetDir.exists() && (targetDir.listFiles()?.isNotEmpty() == true)) {
                        throw IllegalStateException("Conflict detected: Destination directory already exists and is not empty: ${targetDir.relativeTo(rootDir)}")
                    }
                }
            }

        // Room schema conflicts
        rootDir.walkTopDown()
            .onEnter { it.name !in excludedDirectories }
            .filter { it.isDirectory && it.name == "schemas" }
            .forEach { schemasDir ->
                schemasDir.listFiles { f -> f.isDirectory }?.forEach { schemaDir ->
                    if (schemaDir.name.contains(oldPackage)) {
                        val newSchemaName = schemaDir.name.replace(oldPackage, newPackage)
                        val targetDir = File(schemasDir, newSchemaName)
                        if (targetDir.exists() && (targetDir.listFiles()?.isNotEmpty() == true)) {
                            throw IllegalStateException("Conflict detected in Room schemas: ${targetDir.relativeTo(rootDir)} already exists.")
                        }
                    }
                }
            }
    }

    fun applyTransformations(
        stringTransformations: List<Pair<String, String>>,
        regexTransformations: List<Pair<Regex, String>> = emptyList()
    ) {
        rootDir.walkTopDown()
            .onEnter { it.name !in excludedDirectories }
            .filter { it.isFile && it.extension in allowedExtensions }
            .filter { it.name != "starter.properties" && it.name != "gradlew" && it.name != "gradlew.bat" }
            .forEach { file ->
                var content = file.readText()
                var modified = false

                // Regex transformations first
                regexTransformations.forEach { (regex, replacement) ->
                    if (regex.containsMatchIn(content)) {
                        content = regex.replace(content, replacement)
                        modified = true
                    }
                }

                // String transformations
                stringTransformations.forEach { (old, new) ->
                    if (content.contains(old)) {
                        content = content.replace(old, new)
                        modified = true
                    }
                }

                if (modified) {
                    if (dryRun) {
                        println("   [DRY RUN] Would update: ${file.relativeTo(rootDir)}")
                    } else {
                        file.writeText(content)
                        println("   Updated: ${file.relativeTo(rootDir)}")
                    }
                }
            }
    }

    fun movePackageDirectories(
        oldPackage: String,
        newPackage: String,
    ) {
        val srcDirs = getSourceFolders()
        val oldPathSuffix = oldPackage.replace(".", File.separator)
        val newPath = newPackage.replace(".", File.separator)

        rootDir.walkTopDown()
            .onEnter { it.name !in excludedDirectories }
            .filter { it.isDirectory && it.path.endsWith(oldPathSuffix) }
            .filter { dir -> isInsideSourceFolder(dir, srcDirs) }
            .toList()
            .forEach { dir ->
                val rootSrcDir = findRootSrcDir(dir, srcDirs)
                if (rootSrcDir != null) {
                    val targetDir = File(rootSrcDir, newPath)
                    if (dryRun) {
                        println("   [DRY RUN] Would move content from: ${dir.relativeTo(rootDir)} to ${targetDir.relativeTo(rootDir)}")
                    } else {
                        if (!targetDir.exists()) {
                            if (!targetDir.mkdirs()) {
                                throw IllegalStateException("Failed to create directory: ${targetDir.absolutePath}")
                            }
                        }

                        dir.listFiles()?.forEach { file ->
                            val destFile = File(targetDir, file.name)
                            if (destFile.exists()) {
                                throw IllegalStateException("Conflict detected during move: ${destFile.absolutePath} already exists.")
                            }
                            if (!file.renameTo(destFile)) {
                                throw IllegalStateException("Failed to move file: ${file.absolutePath} to ${destFile.absolutePath}")
                            }
                        }

                        if (dir.listFiles()?.isEmpty() == true) {
                            if (!dir.delete()) {
                                println("   ⚠️  Warning: Could not delete empty directory: ${dir.absolutePath}")
                            }
                        }

                        cleanEmptyParents(dir.parentFile, rootSrcDir)
                        println("   Moved content from: ${dir.relativeTo(rootDir)} to ${targetDir.relativeTo(rootDir)}")
                    }
                }
            }
    }

    fun renameRoomSchemas(oldPackage: String, newPackage: String) {
        rootDir.walkTopDown()
            .onEnter { it.name !in excludedDirectories }
            .filter { it.isDirectory && it.name == "schemas" }
            .forEach { schemasDir ->
                schemasDir.listFiles { f -> f.isDirectory }?.forEach { schemaDir ->
                    if (schemaDir.name.contains(oldPackage)) {
                        val newSchemaName = schemaDir.name.replace(oldPackage, newPackage)
                        val targetDir = File(schemasDir, newSchemaName)

                        if (dryRun) {
                            println("   [DRY RUN] Would rename Room schema directory: ${schemaDir.relativeTo(rootDir)} -> $newSchemaName")
                        } else {
                            if (!schemaDir.renameTo(targetDir)) {
                                throw IllegalStateException("Failed to rename Room schema directory: ${schemaDir.absolutePath}")
                            }
                            println("   Renamed Room schema directory: ${targetDir.relativeTo(rootDir)}")
                        }
                    }
                }
            }
    }

    fun escapeXml(input: String): String {
        return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun getSourceFolders() = listOf(
        "src/main/java", "src/main/kotlin",
        "src/test/java", "src/test/kotlin",
        "src/androidTest/java", "src/androidTest/kotlin"
    )

    private fun isInsideSourceFolder(dir: File, srcDirs: List<String>): Boolean {
        val normalizedPath = dir.absolutePath.replace(File.separator, "/")
        return srcDirs.any { marker -> normalizedPath.contains("/$marker/") || normalizedPath.endsWith("/$marker") }
    }

    private fun findRootSrcDir(dir: File, srcDirs: List<String>): File? {
        var current: File? = dir
        while (current != null && current != rootDir) {
            val relativePath = current.relativeTo(rootDir).path.replace(File.separator, "/")
            if (srcDirs.contains(relativePath) || srcDirs.any { relativePath.endsWith("/$it") }) {
                return current
            }
            current = current.parentFile
        }
        return null
    }

    private fun cleanEmptyParents(
        dir: File?,
        root: File,
    ) {
        var current = dir
        while (current != null && current != root && current.isDirectory && (current.listFiles()?.isEmpty() ?: true)) {
            val toDelete = current
            current = current.parentFile
            if (!toDelete.delete()) break
        }
    }
}

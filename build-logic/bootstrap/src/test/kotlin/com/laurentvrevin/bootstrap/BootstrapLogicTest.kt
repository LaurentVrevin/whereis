package com.laurentvrevin.bootstrap

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class BootstrapLogicTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `validateProjectName accepts valid PascalCase`() {
        val logic = BootstrapLogic(tempFolder.root)
        logic.validateProjectName("MyNewProject")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validateProjectName rejects spaces`() {
        val logic = BootstrapLogic(tempFolder.root)
        logic.validateProjectName("My New Project")
    }

    @Test
    fun `validatePackageName accepts valid package`() {
        val logic = BootstrapLogic(tempFolder.root)
        logic.validatePackageName("com.test.app", "com.old.app")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validatePackageName rejects same as source`() {
        val logic = BootstrapLogic(tempFolder.root)
        logic.validatePackageName("com.old.app", "com.old.app")
    }

    @Test
    fun `applyTransformations correctly handles regex and independent names`() {
        val settingsFile = tempFolder.newFile("settings.gradle.kts")
        settingsFile.writeText("rootProject.name = \"AndroidStarter\"")

        val stringsFile = tempFolder.newFile("strings.xml")
        stringsFile.writeText("<string name=\"app_name\">AndroidStarter</string>")

        val logic = BootstrapLogic(tempFolder.root)
        val regexTransformations = listOf(
            Regex("rootProject\\.name\\s*=\\s*\"AndroidStarter\"") to "rootProject.name = \"MonApplication\"",
            Regex("<string name=\"app_name\">AndroidStarter</string>") to "<string name=\"app_name\">Mon application</string>"
        )
        val stringTransformations = listOf(
            "AndroidStarter" to "MonApplication"
        )

        logic.applyTransformations(stringTransformations, regexTransformations)

        val settingsContent = settingsFile.readText()
        val stringsContent = stringsFile.readText()

        assertTrue("Settings should have MonApplication: $settingsContent", settingsContent.contains("rootProject.name = \"MonApplication\""))
        assertTrue("Strings should have Mon application: $stringsContent", stringsContent.contains("<string name=\"app_name\">Mon application</string>"))
    }

    @Test(expected = IllegalStateException::class)
    fun `checkConflicts detects non-empty destination`() {
        val root = tempFolder.root
        val srcDir = File(root, "app/src/main/java").apply { mkdirs() }
        File(srcDir, "com/old/app").apply { mkdirs() }

        val targetDir = File(srcDir, "com/new/app").apply { mkdirs() }
        File(targetDir, "Existing.kt").writeText("data")

        val logic = BootstrapLogic(root)
        logic.checkConflicts("com.old.app", "com.new.app")
    }

    @Test
    fun `movePackageDirectories works in multi-module paths`() {
        val root = tempFolder.root
        val appSrc = File(root, "app/src/main/java").apply { mkdirs() }
        val coreSrc = File(root, "core/src/main/kotlin").apply { mkdirs() }

        val oldAppDir = File(appSrc, "com/old/app").apply { mkdirs() }
        File(oldAppDir, "App.kt").writeText("content")

        val oldCoreDir = File(coreSrc, "com/old/app").apply { mkdirs() }
        File(oldCoreDir, "Core.kt").writeText("content")

        val logic = BootstrapLogic(root)
        logic.movePackageDirectories("com.old.app", "com.new.app")

        val newAppDir = File(appSrc, "com/new/app")
        val newCoreDir = File(coreSrc, "com/new/app")

        assertTrue("App target should exist", newAppDir.exists())
        assertTrue("Core target should exist", newCoreDir.exists())
        assertTrue(File(newAppDir, "App.kt").exists())
        assertTrue(File(newCoreDir, "Core.kt").exists())

        assertFalse("Old App dir should be deleted", oldAppDir.exists())
        assertFalse("Old Core dir should be deleted", oldCoreDir.exists())
    }

    @Test
    fun `renameRoomSchemas renames schema directories`() {
        val root = tempFolder.root
        val schemasDir = File(root, "data/schemas").apply { mkdirs() }
        val oldSchemaDir = File(schemasDir, "com.old.app.AppDatabase").apply { mkdirs() }
        File(oldSchemaDir, "1.json").writeText("{\"package\": \"com.old.app\"}")

        val logic = BootstrapLogic(root)
        logic.renameRoomSchemas("com.old.app", "com.new.app")

        val newSchemaDir = File(schemasDir, "com.new.app.AppDatabase")
        assertTrue("New schema directory should exist", newSchemaDir.exists())
        assertTrue("Schema file should exist", File(newSchemaDir, "1.json").exists())
        assertFalse("Old schema directory should be deleted", oldSchemaDir.exists())
    }

    @Test
    fun `escapeXml correctly escapes special characters`() {
        val logic = BootstrapLogic(tempFolder.root)
        assertEquals("App &amp; Tools", logic.escapeXml("App & Tools"))
        assertEquals("Test &lt;beta&gt;", logic.escapeXml("Test <beta>"))
        assertEquals("&quot;Quote&quot;", logic.escapeXml("\"Quote\""))
        assertEquals("&apos;Apos&apos;", logic.escapeXml("'Apos'"))
    }

    @Test
    fun `applyTransformations excludes build folders`() {
        val buildDir = tempFolder.newFolder("build")
        val file = File(buildDir, "should_not_be_touched.kt")
        file.writeText("AndroidStarter")

        val logic = BootstrapLogic(tempFolder.root)
        logic.applyTransformations(listOf("AndroidStarter" to "NewName"))

        assertEquals("AndroidStarter", file.readText())
    }

    @Test
    fun `applyTransformations excludes dot artifacts and dot idea`() {
        val root = tempFolder.root
        val artifactsDir = File(root, ".artifacts/session").apply { mkdirs() }
        val artifactFile = File(artifactsDir, "implementation_plan.artifact.md")
        artifactFile.writeText("com.laurentvrevin.androidstarter")

        val ideaDir = File(root, ".idea").apply { mkdirs() }
        val ideaFile = File(ideaDir, "workspace.xml")
        ideaFile.writeText("com.laurentvrevin.androidstarter")

        val logic = BootstrapLogic(root)
        logic.applyTransformations(listOf("com.laurentvrevin.androidstarter" to "com.new.app"))

        assertEquals("com.laurentvrevin.androidstarter", artifactFile.readText())
        assertEquals("com.laurentvrevin.androidstarter", ideaFile.readText())
    }

    @Test
    fun `dryRun does not modify anything`() {
        val file = tempFolder.newFile("test.kt")
        file.writeText("AndroidStarter")

        val logic = BootstrapLogic(tempFolder.root, dryRun = true)
        logic.applyTransformations(listOf("AndroidStarter" to "NewName"))

        assertEquals("AndroidStarter", file.readText())
    }
}

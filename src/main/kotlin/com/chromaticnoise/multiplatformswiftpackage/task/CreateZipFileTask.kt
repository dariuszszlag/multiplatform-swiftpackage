package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileTaskHelper.getZipFile
import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip

internal fun Project.registerCreateZipFileTask() {
    tasks.register("createZipFile", Zip::class.java) {
        setGroup(null) // hide the task from the task list
        description = "Creates a ZIP file containing the XCFramework"

        dependsOn("createXCFramework")

        val projectConfiguration = getConfigurationOrThrow()
        val outputDirectory = projectConfiguration.outputDirectory.value
        val packageName = projectConfiguration.packageName
        val xcFrameworkPath = file("$outputDirectory/${packageName.value}.xcframework")

        from(xcFrameworkPath)
        destinationDirectory.set(outputDirectory)
        archiveFileName.set(getZipFile(projectConfiguration).name)
    }
}

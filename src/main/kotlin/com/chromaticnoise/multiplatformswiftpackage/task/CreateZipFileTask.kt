package com.chromaticnoise.multiplatformswiftpackage.task

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
        val distributionMode = projectConfiguration.distributionMode
        val packageName = projectConfiguration.packageName
        val versionName = projectConfiguration.versionName
        val zipFileName = projectConfiguration.zipFileName.getName(distributionMode, "${packageName.nameWithSuffix}-${versionName.value}")
        val zipFilePath = file("$outputDirectory/$zipFileName")
        val xcFrameworkPath = file("$outputDirectory/$packageName.xcframework")

        from(xcFrameworkPath.name)
        destinationDirectory.set(outputDirectory)
        archiveFileName.set(zipFilePath.name)
    }
}

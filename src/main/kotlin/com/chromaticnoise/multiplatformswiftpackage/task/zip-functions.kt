package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File

internal fun zipFileChecksum(
    project: Project,
    outputDirectory: OutputDirectory,
): String {
    val outputPath = outputDirectory.value
    return File(
        outputPath,
        project.getZippedXCFrameworkName()
    )
        .takeIf { it.exists() }
        ?.let { zipFile ->
            ByteArrayOutputStream().use { os ->
                project.exec {
                    workingDir = outputPath
                    executable = "swift"
                    args = listOf("package", "compute-checksum", zipFile.name)
                    standardOutput = os
                }
                os.toString()
            }
        } ?: ""
}

internal fun Project.getZippedXCFrameworkName(): String = with(getConfigurationOrThrow()){
    val packageName = packageName.nameWithSuffix
    val versionName = versionName.value
    return when (distributionMode) {
        DistributionMode.Maven -> zipFileName.getName(true, "$packageName-$versionName")
        else -> zipFileName.getName()
    }
}
package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileName
import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File

internal fun zipFileChecksum(
    project: Project,
    distributionMode: DistributionMode,
    outputDirectory: OutputDirectory,
    zipFileName: ZipFileName
): String {
    val outputPath = outputDirectory.value
    return File(
        outputPath,
        zipFileName.getName(
            distributionMode is DistributionMode.Maven,
            project.getConfigurationOrThrow().let {
                it.let { "${it.packageName.nameWithSuffix}-${it.versionName.value}" }
            }
        )
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

package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import com.chromaticnoise.multiplatformswiftpackage.domain.PackageName
import com.chromaticnoise.multiplatformswiftpackage.domain.VersionName
import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileName
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File

internal fun zipFileChecksum(
    project: Project,
    distributionMode: DistributionMode,
    outputDirectory: OutputDirectory,
    zipFileName: ZipFileName,
    packageName: PackageName,
    versionName: VersionName
): String {
    val outputPath = outputDirectory.value
    val zipFilePath = zipFileName.getName(distributionMode, "${packageName.nameWithSuffix}-${versionName.value}")
    return File(
        outputPath,
        zipFilePath
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

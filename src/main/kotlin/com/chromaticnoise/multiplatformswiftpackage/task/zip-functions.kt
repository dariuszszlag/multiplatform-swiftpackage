package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import com.chromaticnoise.multiplatformswiftpackage.domain.PackageName
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.VersionName
import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileName
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File

internal fun Project.zipFileChecksum(
    pluginConfiguration: PluginConfiguration
): String = getZipFile(pluginConfiguration)
        .takeIf { it.exists() }
        ?.let { zipFile ->
            ByteArrayOutputStream().use { os ->
                exec {
                    workingDir = pluginConfiguration.outputDirectory.value
                    executable = "swift"
                    args = listOf("package", "compute-checksum", zipFile.name)
                    standardOutput = os
                }
                os.toString()
            }
        } ?: ""

internal fun Project.getZipFile(pluginConfiguration: PluginConfiguration) = getZipFileBuilder(
    pluginConfiguration.outputDirectory,
    pluginConfiguration.distributionMode,
    pluginConfiguration.zipFileName,
    pluginConfiguration.packageName,
    pluginConfiguration.versionName
)

private fun Project.getZipFileBuilder(
    outputDirectory: OutputDirectory,
    distributionMode: DistributionMode,
    zipFileName: ZipFileName,
    packageName: PackageName,
    versionName: VersionName
): File {
    val outputPath = outputDirectory.value
    val zipFileNamed = zipFileName.getName(distributionMode, "${packageName.nameWithSuffix}-${versionName.value}")
    val zipFilePath = file("$outputDirectory/$zipFileNamed")
    return File(outputPath, zipFilePath.name)
}
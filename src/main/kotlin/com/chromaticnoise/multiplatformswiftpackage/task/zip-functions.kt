package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import com.chromaticnoise.multiplatformswiftpackage.domain.PackageName
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.VersionName
import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileName
import org.gradle.api.Project
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset

internal fun Project.zipFileChecksum(
    pluginConfiguration: PluginConfiguration
): String {
    val zipFile = getZipFile(pluginConfiguration)
    val os = ByteArrayOutputStream()
    return if (zipFile.exists()) {
        exec {
            getSha256(zipFile)
            standardOutput = os
        }
        os.toByteArray().toString(Charset.defaultCharset()).trim()
    } else {
        ""
    }
}

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
    val zipFilePath = file("$outputPath/$zipFileNamed")
    return File(outputPath, zipFilePath.name)
}

private fun ExecSpec.getSha256(file: File): ExecSpec? {
    val filePath = file.path
    return commandLine(
        "shasum",
        "-a",
        "256",
        filePath,
        "|",
        "sed",
        "'s/ .*//'"
    )
}

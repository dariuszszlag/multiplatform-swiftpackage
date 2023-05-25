package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import com.chromaticnoise.multiplatformswiftpackage.domain.PackageName
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageConfiguration
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
    val outputPath = pluginConfiguration.outputDirectory.value
    val packageSwiftFileName = SwiftPackageConfiguration.FILE_NAME
    val packageSwiftFile = file("$outputPath/$packageSwiftFileName")
    val hadPackageSwift = packageSwiftFile.exists()
    if (!hadPackageSwift) {
        packageSwiftFile.writeText("")
    }
    exec {
        getSha256(zipFile)
        standardOutput = os
    }
    if (!hadPackageSwift) {
        packageSwiftFile.delete()
    }
    return os.toByteArray().toString(Charset.defaultCharset()).trim()
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
        "swift",
        "package",
        "compute-checksum",
        filePath
    )
}

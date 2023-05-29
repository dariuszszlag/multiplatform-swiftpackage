package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileTaskHelper.zipFileChecksum
import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import com.chromaticnoise.multiplatformswiftpackage.domain.konanTarget
import com.chromaticnoise.multiplatformswiftpackage.domain.swiftPackagePlatformName
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project

internal fun Project.registerCreateSwiftPackageTask() {
    tasks.register("createSwiftPackage") {
        group = "multiplatform-swift-package"
        description = "Creates a Swift package to distribute an XCFramework"

        dependsOn("createXCFramework")
        dependsOn("createZipFile")

        doLast {
            val configuration = getConfigurationOrThrow()
            val outputPath = configuration.outputDirectory.value
            val packageSwiftFileName = SwiftPackageConfiguration.FILE_NAME
            val packageFile = file("$outputPath/$packageSwiftFileName")

            val zipFileChecksum = zipFileChecksum(configuration)

            val packageConfiguration = SwiftPackageConfiguration(
                project = project,
                packageName = configuration.packageName,
                versionName = configuration.versionName,
                toolVersion = configuration.swiftToolsVersion,
                platforms = platforms(configuration),
                distributionMode = configuration.distributionMode,
                zipChecksum = zipFileChecksum,
                zipFileName = configuration.zipFileName
            )

            if (zipFileChecksum != "") SimpleTemplateEngine()
                .createTemplate(SwiftPackageConfiguration.templateFile)
                .make(packageConfiguration.templateProperties)
                .writeTo(packageFile.writer())
        }
    }
}

private fun platforms(configuration: PluginConfiguration): String =
    configuration.targetPlatforms.flatMap { platform ->
        configuration.appleTargets
            .filter { appleTarget -> platform.targets.firstOrNull { it.konanTarget == appleTarget.nativeTarget.konanTarget } != null }
            .mapNotNull { target -> target.nativeTarget.konanTarget.family.swiftPackagePlatformName }
            .distinct()
            .map { platformName -> ".$platformName(.v${platform.version.name})" }
    }.joinToString(",\n")

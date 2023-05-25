package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileName
import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import com.chromaticnoise.multiplatformswiftpackage.domain.konanTarget
import com.chromaticnoise.multiplatformswiftpackage.domain.swiftPackagePlatformName
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import java.io.File

internal fun Project.registerCreateSwiftPackageTask() {
    tasks.register("createSwiftPackage") {
        group = "multiplatform-swift-package"
        description = "Creates a Swift package to distribute an XCFramework"

        dependsOn("createXCFramework")
        dependsOn("createZipFile")

        doLast {
            val configuration = getConfigurationOrThrow()
            val packageFile = File(
                configuration.outputDirectory.value,
                SwiftPackageConfiguration.FILE_NAME
            ).apply {
                parentFile.mkdirs()
                createNewFile()
            }

            val packageConfiguration = SwiftPackageConfiguration(
                project = project,
                packageName = configuration.packageName,
                versionName = configuration.versionName,
                toolVersion = configuration.swiftToolsVersion,
                platforms = platforms(configuration),
                distributionMode = configuration.distributionMode,
                zipChecksum = zipFileChecksum(
                    project,
                    configuration.distributionMode,
                    configuration.outputDirectory,
                    configuration.zipFileName,
                    configuration.packageName,
                    configuration.versionName
                ),
                zipFileName = ZipFileName.of("${configuration.packageName.nameWithSuffix}-${configuration.versionName.value}").orNull!!
            )

            SimpleTemplateEngine()
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

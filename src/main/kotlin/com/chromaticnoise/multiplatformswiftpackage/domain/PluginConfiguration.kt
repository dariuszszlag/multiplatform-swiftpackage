package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.SwiftPackageExtension
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.BlankPackageName
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.MissingAppleTargets
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.MissingSwiftToolsVersion
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.MissingTargetPlatforms
import org.gradle.api.Project

internal class PluginConfiguration private constructor(
    val buildConfiguration: BuildConfiguration,
    val packageName: PackageName,
    val versionName: VersionName,
    val outputDirectory: OutputDirectory,
    val swiftToolsVersion: SwiftToolVersion,
    val distributionMode: DistributionMode,
    val targetPlatforms: Collection<TargetPlatform>,
    val appleTargets: Collection<AppleTarget>,
    val zipFileName: ZipFileName
) {
    internal companion object {
        fun of(extension: SwiftPackageExtension): Either<List<PluginConfigurationError>, PluginConfiguration> {
            val targetPlatforms = extension.targetPlatforms.platforms
            val packageName = extension.getPackageName()
            val versionName = extension.versionName

            val errors = mutableListOf<PluginConfigurationError>().apply {
                if (extension.swiftToolsVersion == null) {
                    add(MissingSwiftToolsVersion)
                }

                val targetPlatformErrors = extension.targetPlatforms.errors
                if (targetPlatformErrors.isNotEmpty()) {
                    addAll(targetPlatformErrors)
                }

                if (targetPlatformErrors.isEmpty() && targetPlatforms.isEmpty()) {
                    add(MissingTargetPlatforms)
                }

                if (extension.appleTargets.isEmpty() && targetPlatforms.isNotEmpty()) {
                    add(MissingAppleTargets)
                }

                packageName.leftValueOrNull?.let { error -> add(error) }

                extension.zipFileName?.leftValueOrNull?.let { error -> add(error) }

                versionName?.leftValueOrNull?.let { error -> add(error) }

            }

            return if (errors.isEmpty()) {
                Either.Right(
                    PluginConfiguration(
                        extension.buildConfiguration,
                        packageName.orNull!!,
                        versionName?.orNull ?: getDestinationProjectVersion(extension.project),
                        extension.outputDirectory,
                        extension.swiftToolsVersion!!,
                        extension.distributionMode,
                        targetPlatforms,
                        extension.appleTargets,
                        extension.zipFileName?.orNull ?: defaultZipFileName(
                            packageName.orNull!!,
                            extension.project
                        )
                    )
                )
            } else {
                Either.Left(errors)
            }
        }

        private fun SwiftPackageExtension.getPackageName(): Either<PluginConfigurationError, PackageName> =
            packageName
                ?: appleTargets.map { it.getFramework(buildConfiguration) }.firstOrNull()
                    ?.let { framework ->
                        PackageName.of(framework.name.value)
                    } ?: Either.Left(BlankPackageName)

        private fun defaultZipFileName(packageName: PackageName, project: Project) =
            ZipFileName.of("${packageName.value}-${project.version}").orNull!!

        private fun getDestinationProjectVersion(project: Project) = VersionName.of(project.version.toString()).orNull!!
    }

    internal sealed class PluginConfigurationError {
        object MissingSwiftToolsVersion : PluginConfigurationError()
        data class InvalidTargetName(val name: String) : PluginConfigurationError()
        object MissingTargetPlatforms : PluginConfigurationError()
        object MissingAppleTargets : PluginConfigurationError()
        object BlankPackageName : PluginConfigurationError()
        object BlankVersionName : PluginConfigurationError()
        object BlankZipFileName : PluginConfigurationError()
    }
}

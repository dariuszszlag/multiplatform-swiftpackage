package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.AppleTarget
import com.chromaticnoise.multiplatformswiftpackage.domain.BuildConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.Either
import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import com.chromaticnoise.multiplatformswiftpackage.domain.PackageName
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftToolVersion
import com.chromaticnoise.multiplatformswiftpackage.domain.TargetPlatform
import com.chromaticnoise.multiplatformswiftpackage.domain.VersionName
import com.chromaticnoise.multiplatformswiftpackage.domain.ZipFileName
import com.chromaticnoise.multiplatformswiftpackage.dsl.BuildConfigurationDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.DistributionModeDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.TargetPlatformDsl
import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import java.io.File

open class SwiftPackageExtension(internal val project: Project) {

    internal var buildConfiguration: BuildConfiguration = BuildConfiguration.Release
    internal var packageName: Either<PluginConfigurationError, PackageName>? = null
    internal var outputDirectory: OutputDirectory? = null
    internal var swiftToolsVersion: SwiftToolVersion? = null
    internal var distributionMode: DistributionMode = DistributionMode.Local
    internal var targetPlatforms: Collection<Either<List<PluginConfigurationError>, TargetPlatform>> =
        emptyList()
    internal var appleTargets: Collection<AppleTarget> = emptyList()
    internal var zipFileName: Either<PluginConfigurationError, ZipFileName>? = null
    internal var versionName: Either<PluginConfigurationError, VersionName>? = null

    /**
     * Sets the name of the Swift package.
     * Defaults to the base name of the first framework found in the project.
     *
     * @param name of the Swift package.
     */
    fun packageName(name: String) {
        packageName = PackageName.of(name)
    }

    /**
     * Sets the directory where files like the Package.swift and XCFramework will be created.
     * Defaults to $projectDir/swiftpackage
     *
     * @param directory where the files will be created.
     */
    fun outputDirectory(directory: File) {
        outputDirectory = OutputDirectory(directory)
    }

    /**
     * Version of the Swift tools. That's the version added to the Package.swift header.
     * E.g. 5.3
     */
    fun swiftToolsVersion(name: String) {
        swiftToolsVersion = SwiftToolVersion.of(name)
    }

    /**
     * Version of the target. That's the version added to the Package.swift.
     * E.g. 1.0.0
     */
    fun versionName(name: String) {
        versionName = VersionName.of(name)
    }

    /**
     * Builder for the [BuildConfiguration].
     */
    fun buildConfiguration(configure: BuildConfigurationDSL.() -> Unit) {
        BuildConfigurationDSL().also { dsl ->
            dsl.configure()
            buildConfiguration = dsl.buildConfiguration
        }
    }

    fun buildConfiguration(configure: Closure<BuildConfigurationDSL>) {
        buildConfiguration { ConfigureUtil.configure(configure, this) }
    }

    /**
     * Builder for the [DistributionMode].
     */
    fun distributionMode(configure: DistributionModeDSL.() -> Unit) {
        DistributionModeDSL().also { dsl ->
            dsl.configure()
            distributionMode = dsl.distributionMode
        }
    }

    fun distributionMode(configure: Closure<DistributionModeDSL>) {
        distributionMode { ConfigureUtil.configure(configure, this) }
    }

    /**
     * Builder for instances of [TargetPlatform].
     */
    fun targetPlatforms(configure: TargetPlatformDsl.() -> Unit) {
        TargetPlatformDsl().also { dsl ->
            dsl.configure()
            targetPlatforms = dsl.targetPlatforms
        }
    }

    fun targetPlatforms(configure: Closure<TargetPlatformDsl>) {
        targetPlatforms { ConfigureUtil.configure(configure, this) }
    }

    /**
     * Sets the name of the ZIP file.
     * Do not append the `.zip` file extension since it will be added during the build.
     *
     * Defaults to the [packageName] concatenated with the project version. E.g.
     * MyAwesomeKit-2.3.42-SNAPSHOT
     */
    fun zipFileName(name: String) {
        zipFileName = ZipFileName.of(name)
    }
}

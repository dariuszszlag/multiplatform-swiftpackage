package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.MultiplatformSwiftPackagePlugin
import com.chromaticnoise.multiplatformswiftpackage.domain.MavenDistribution.createMavenDistributionUrl
import org.gradle.api.Project

internal data class SwiftPackageConfiguration(
    private val project: Project,
    private val packageName: PackageName,
    private val versionName: VersionName,
    private val toolVersion: SwiftToolVersion,
    private val platforms: String,
    private val distributionMode: DistributionMode,
    private val zipChecksum: String,
    private val zipFileName: ZipFileName
) {

    private val distributionUrl = when (distributionMode) {
        is DistributionMode.Local -> null
        is DistributionMode.Maven -> createMavenDistributionUrl(project, versionName, packageName)
        is DistributionMode.Remote -> distributionMode.url.appendPath(zipFileName.getName(distributionMode is DistributionMode.Maven)).value
    }

    internal val templateProperties = mapOf(
        "toolsVersion" to toolVersion.name,
        "name" to packageName.value,
        "platforms" to platforms,
        "isLocal" to (distributionMode == DistributionMode.Local),
        "url" to distributionUrl,
        "checksum" to zipChecksum.trim()
    )

    internal companion object {
        internal const val FILE_NAME = "Package.swift"

        internal val templateFile =
            MultiplatformSwiftPackagePlugin::class.java.getResource("/templates/Package.swift.template")
    }
}

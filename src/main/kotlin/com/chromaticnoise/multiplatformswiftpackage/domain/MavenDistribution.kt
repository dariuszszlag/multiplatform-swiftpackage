package com.chromaticnoise.multiplatformswiftpackage.domain

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

internal object MavenDistribution {

    private val String.slashTerminatedValue: String
        get() = takeIf { it.endsWith("/") } ?: "$this/"

    internal fun createMavenDistributionUrl(project: Project, versionName: VersionName, packageName: PackageName): String {
        val mavenPublishingExtension = project.extensions.getByType<PublishingExtension>()
        val repository = mavenPublishingExtension.repositories.findByName(packageName.value) as MavenArtifactRepository
        val url = repository.url.toASCIIString()
        val version = versionName.value
        val group = project.group.toString()
        val artifactId = packageName.value
        return artifactPath(url.slashTerminatedValue, group, artifactId, version)
    }

    private fun artifactPath(url: String, group: String, artifactId: String, version: String) =
        "$url/$group/$artifactId/$version/$artifactId-$version.zip"
}

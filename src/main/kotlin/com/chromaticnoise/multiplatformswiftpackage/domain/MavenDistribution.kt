package com.chromaticnoise.multiplatformswiftpackage.domain

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

internal object MavenDistribution {

    private val String.slashTerminatedValue: String
        get() = takeIf { it.endsWith("/") } ?: "$this/"

    internal fun createMavenDistributionUrl(project: Project, versionName: VersionName, packageName: PackageName): String {
        val mavenPublishingExtension = project.extensions.getByType<PublishingExtension>()
        val repository =
            mavenPublishingExtension.repositories.filterIsInstance<MavenArtifactRepository>().firstOrNull() ?:
            mavenPublishingExtension.repositories.findByName(packageName.value) as MavenArtifactRepository ?:
            throw GradleException("Artifact repository not found, please, specify maven repository")
        val url = repository.url.toString()
        val version = versionName.value
        val group = project.group.toString()
        val artifactId = packageName.nameWithSuffix
        return artifactPath(url.slashTerminatedValue, group, artifactId, version)
    }

    private fun artifactPath(url: String, group: String, artifactId: String, version: String) =
        "$url/${group.replace(".", "/")}/$artifactId/$version/$artifactId-$version.zip"
}

package com.chromaticnoise.multiplatformswiftpackage.domain

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

internal object MavenDistributionHelper {

    private val String.slashTerminatedValue: String
        get() = takeIf { it.endsWith("/") } ?: "$this/"

    internal fun createMavenDistributionUrl(project: Project, versionName: VersionName, packageName: PackageName): String {
        val repository = project.getMavenArtifactRepository(packageName)
        val url = repository.url.toString()
        val version = versionName.value
        val group = project.group.toString()
        val artifactId = packageName.nameWithSuffix
        return artifactPath(url.slashTerminatedValue, group, artifactId, version)
    }

    private fun Project.getMavenArtifactRepository(packageName: PackageName): MavenArtifactRepository {
        val mavenPublishingExtension = extensions.getByType<PublishingExtension>()
        return mavenPublishingExtension.repositories.filterIsInstance<MavenArtifactRepository>().firstOrNull() ?:
            mavenPublishingExtension.repositories.findByName(packageName.value) as MavenArtifactRepository
    }

    private fun artifactPath(url: String, group: String, artifactId: String, version: String) =
        "$url${group.replace(".", "/")}/$artifactId/$version/$artifactId-$version.zip"
}

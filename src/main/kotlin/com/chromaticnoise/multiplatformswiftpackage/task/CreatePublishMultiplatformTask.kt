package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.getByType

internal fun Project.registerPublishMultiplatformTask() {
    val projectGroup = group
    val mavenPublishingExtension = extensions.getByType<PublishingExtension>()
    val projectConfiguration = getConfigurationOrThrow()
    val zipFileArtifact = tasks.named("createZipFile", Zip::class.java).flatMap {
        it.archiveFile
    }
    mavenPublishingExtension.publications.create(projectConfiguration.packageName.value, MavenPublication::class.java) {
        version = projectConfiguration.versionName.value
        group = projectGroup
        artifactId = projectConfiguration.packageName.nameWithSuffix
        artifact(zipFileArtifact)
        dependCreatingSwiftPackageOnMavenPublishTask()
    }
}

private fun Project.dependCreatingSwiftPackageOnMavenPublishTask() {
    val projectConfiguration = getConfigurationOrThrow()
    val publicationName = projectConfiguration.packageName.value.capitalize()
    val publishTaskName = "publish${publicationName}PublicationToGitHubPackagesRepository" //temporarily set only GitHubPackages
    return tasks.named("createSwiftPackage").configure { dependsOn(tasks.named(publishTaskName)) }
}
package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType

internal fun Project.registerPublishMultiplatformTask() {
    val projectGroup = group
    val mavenPublishingExtension = extensions.getByType<PublishingExtension>()
    val projectConfiguration = getConfigurationOrThrow()
    val distributionMode = projectConfiguration.distributionMode
    val packageName = projectConfiguration.packageName
    val versionName = projectConfiguration.versionName
    mavenPublishingExtension.publications.create(projectConfiguration.packageName.value, MavenPublication::class.java) {
        version = projectConfiguration.versionName.value
        group = projectGroup
        artifactId = projectConfiguration.packageName.nameWithSuffix
        artifact(projectConfiguration.zipFileName.getName(distributionMode, "${packageName.nameWithSuffix}-${versionName.value}"))
        publishingTasks.forEach {
            tasks.named("createSwiftPackage").configure { dependsOn(it) }
        }
    }
}

private val Project.publishingTasks: List<TaskProvider<Task>> get() {
    val mavenPublishingExtension = extensions.getByType<PublishingExtension>()
    val projectConfiguration = getConfigurationOrThrow()
    val publicationName = mavenPublishingExtension.publications.getByName(projectConfiguration.packageName.value).name.capitalize()
    return mavenPublishingExtension.repositories.filterIsInstance<MavenArtifactRepository>().map { repo ->
        val repositoryName = repo.name.capitalize()
        val publishTaskName = "publish${publicationName}PublicationTo${repositoryName}Repository"
        tasks.named(publishTaskName)
    }
}
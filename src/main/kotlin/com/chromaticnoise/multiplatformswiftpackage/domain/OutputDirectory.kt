package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.ProjectProcessHelper.findRepoRoot
import com.chromaticnoise.multiplatformswiftpackage.domain.ProjectProcessHelper.isRootDirectoryGitRepository
import org.gradle.api.Project
import java.io.File

internal data class OutputDirectory(val value: File) {

    internal companion object {
        fun of(project: Project, directory: File?): Either<PluginConfiguration.PluginConfigurationError, OutputDirectory> = when {
            project.isRootDirectoryGitRepository() -> Either.Right(OutputDirectory(project.file(project.findRepoRoot())))
            directory != null -> Either.Right(OutputDirectory(directory))
            else -> Either.Left(PluginConfiguration.PluginConfigurationError.BlankOutputDirectory)
        }
    }
}

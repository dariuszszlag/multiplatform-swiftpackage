package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError

internal class ZipFileName private constructor(private val value: String) {

    private val nameWithExtension: String get() = "$value.zip"

    fun getName(buildOnMaven: Boolean = false, newValue: String = "") = if (buildOnMaven) "$newValue.zip" else nameWithExtension

    internal companion object {
        fun of(name: String?): Either<PluginConfigurationError, ZipFileName> =
            name?.ifNotBlank { Either.Right(ZipFileName(it)) }
                ?: Either.Left(PluginConfigurationError.BlankZipFileName)
    }

    override fun equals(other: Any?): Boolean = value == (other as? ZipFileName)?.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "ZipFileName(value='$value')"
}

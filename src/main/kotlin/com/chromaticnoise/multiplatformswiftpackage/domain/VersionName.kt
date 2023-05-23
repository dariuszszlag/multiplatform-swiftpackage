package com.chromaticnoise.multiplatformswiftpackage.domain

internal data class VersionName(val value: String) {

    val name: String get() = value

    internal companion object {
        fun of(value: String?): Either<PluginConfiguration.PluginConfigurationError, VersionName> =
            value?.ifNotBlank { Either.Right(VersionName(it)) }
                ?: Either.Left(PluginConfiguration.PluginConfigurationError.BlankVersionName)
    }

    override fun equals(other: Any?): Boolean = value == (other as? VersionName)?.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString() = "VersionName(value='$value')"
}

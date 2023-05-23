package com.chromaticnoise.multiplatformswiftpackage.domain

internal data class RemoteDistribution(val value: String) {
    private val slashTerminatedValue: String
        get() =
            value.takeIf { it.endsWith("/") } ?: "$value/"

    fun appendPath(path: String) = RemoteDistribution("$slashTerminatedValue$path")
}

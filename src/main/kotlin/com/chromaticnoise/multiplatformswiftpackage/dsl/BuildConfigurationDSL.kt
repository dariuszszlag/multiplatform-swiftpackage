package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.BuildConfiguration

/**
 * DSL to create instance of a [BuildConfiguration].
 */
class BuildConfigurationDSL {
    internal var buildConfiguration: BuildConfiguration = BuildConfiguration.Release

    /**
     * XCode release configuration.
     */
    fun release() {
        buildConfiguration = BuildConfiguration.Release
    }

    /**
     * XCode debug configuration.
     */
    fun debug() {
        buildConfiguration = BuildConfiguration.Debug
    }

    /**
     * Custom configuration.
     *
     * @param name of the custom configuration.
     */
    fun named(name: String) {
        buildConfiguration = BuildConfiguration.Custom(name)
    }
}

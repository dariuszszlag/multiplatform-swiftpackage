package com.chromaticnoise.multiplatformswiftpackage.domain

internal sealed class DistributionMode {
    internal object Local : DistributionMode()
    internal object Maven : DistributionMode()
    internal data class Remote(val url: RemoteDistribution) : DistributionMode()
}

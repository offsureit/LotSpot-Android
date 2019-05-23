package com.oit.lotspot.constants

interface TrackingConst {

    object DURATION_MILLISECONDS {
        const val MIN_BW_LOCATION_UPDATES: Long = 0    // The minimum time between location updates
    }

    object DISTANCE_METER {
        const val MIN_BETWEEN_LOCATION_UPDATES: Float = 25.0f    // The minimum distance between location updates (25m)
        const val ROUTE_NEARBY: Float = 20.0f
    }

}
package com.oit.lotspot.retrofit.response.google

import java.io.Serializable

/**
 * This is the model class which stores Google Direction's route path data
 */
class GoogleRouteModel() : Serializable {
    internal var overview_polyline: GoogleOverviewPolylineModel? = null
}
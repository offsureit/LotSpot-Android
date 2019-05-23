package com.oit.lotspot.retrofit.response.google

import java.io.Serializable

/**
 * This is the model class which stores Google Direction's route path data
 */
class GoogleMapPathModel() : Serializable {
    internal var routes: List<GoogleRouteModel>? = null
}
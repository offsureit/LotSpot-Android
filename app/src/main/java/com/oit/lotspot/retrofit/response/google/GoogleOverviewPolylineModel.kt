package com.oit.lotspot.retrofit.response.google
import java.io.Serializable



/**
 * This is the model class which stores Google Direction's route path data
 */
class GoogleOverviewPolylineModel() : Serializable {
    internal var points: String? = null
}
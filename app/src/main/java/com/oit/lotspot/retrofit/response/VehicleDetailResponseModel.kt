package com.oit.lotspot.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class VehicleDetailResponseModel {

    class VehicleDetailFirstResponseModel {

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("data")
        @Expose
        var data = VehicleDetailDataResponseModel()
    }

    class VehicleDetailDataResponseModel {

        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("year")
        @Expose
        var year: Int? = null

        @SerializedName("make")
        @Expose
        var make: String? = null

        @SerializedName("model")
        @Expose
        var model: String? = null

        @SerializedName("engine")
        @Expose
        var engine: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("vin")
        @Expose
        var vin: String? = null

        @SerializedName("created_at")
        @Expose
        var created_at: String? = null

        @SerializedName("updated_at")
        @Expose
        var updated_at: String? = null

        @SerializedName("lat")
        @Expose
        var lat: Double? = null

        @SerializedName("lng")
        @Expose
        var lng: Double? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        var searchCount = 0
    }

}
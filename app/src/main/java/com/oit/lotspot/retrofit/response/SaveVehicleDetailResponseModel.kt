package com.oit.lotspot.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveVehicleDetailResponseModel {

    @SerializedName("status")
    @Expose
    var status: String? = ""

    @SerializedName("message")
    @Expose
    var message: String? = ""

    @SerializedName("profile")
    @Expose
    var profile = LoginResponseModel.LoginResponseProfileModel()

}
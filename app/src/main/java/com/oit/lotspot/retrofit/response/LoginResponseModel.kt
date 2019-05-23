package com.oit.lotspot.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LoginResponseModel : Serializable {

    class LoginResponseFirstModel {

        @SerializedName("status")
        @Expose
        var status: String? = ""

        @SerializedName("profile")
        @Expose
        var profile = LoginResponseProfileModel()

        @SerializedName("token")
        @Expose
        var token: String? = ""

        @SerializedName("expires_in")
        @Expose
        var expiresIn: Int? = 0
    }


    class LoginResponseProfileModel {

        @SerializedName("id")
        @Expose
        var id: Int? = 0

        @SerializedName("name")
        @Expose
        var name: String? = ""

        @SerializedName("email")
        @Expose
        var email: String? = ""

        @SerializedName("contact")
        @Expose
        var contact: String? = ""

        @SerializedName("address")
        @Expose
        var address: String? = ""

        @SerializedName("userType")
        @Expose
        var userType: String? = ""

        @SerializedName("deviceType")
        @Expose
        var deviceType: String? = ""

        @SerializedName("deviceToken")
        @Expose
        var deviceToken: String? = ""

        @SerializedName("status")
        @Expose
        var status: String? = ""

        @SerializedName("request")
        @Expose
        var request: String? = ""

        @SerializedName("scans")
        @Expose
        var scans: Int? = 0

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = ""

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = ""

        @SerializedName("active_subscriptions")
        @Expose
        var activeSubscriptions: Any? = null
    }

}
package com.oit.lotspot.retrofit.response

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class VehicleListResponseModel {

        @SerializedName("current_page")
        internal var current_page = 1

        @SerializedName("data")
        internal var data = ArrayList<VehicleDetailResponseModel.VehicleDetailDataResponseModel>()

        @SerializedName("first_page_url")
        internal var first_page_url = ""

        @SerializedName("from")
        internal var from = 1

        @SerializedName("last_page")
        internal var last_page = 0

        @SerializedName("last_page_url")
        internal var last_page_url = ""

        @SerializedName("next_page_url")
        internal var next_page_url = ""

        @SerializedName("path")
        internal var path = ""

        @SerializedName("per_page")
        internal var per_page = 0

        @SerializedName("prev_page_url")
        internal var prev_page_url = ""

        @SerializedName("to")
        internal var to = 0

        @SerializedName("total")
        internal var total = 0

}
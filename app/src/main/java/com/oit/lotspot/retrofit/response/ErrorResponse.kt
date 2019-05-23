package com.oit.lotspot.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ErrorResponse : Serializable {


    @SerializedName("error")
    internal var error = ErrorResponseFirstModel()

    class ErrorResponseFirstModel : Serializable {

        @SerializedName("message")
        internal var message = ""

        @SerializedName("status_code")
        internal var status_code = 0

        @SerializedName("errors")
        internal var errors = ErrorResponseSecondModel()

    }

    class ErrorResponseSecondModel : Serializable {

        @SerializedName("contact")
        internal var contact: Array<String>? = null
    }
}
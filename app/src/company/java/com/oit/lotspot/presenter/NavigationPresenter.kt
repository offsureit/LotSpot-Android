package com.oit.lotspot.presenter

import android.util.Log
import com.google.gson.Gson
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.interfaces.ApiInterface
import com.oit.lotspot.retrofit.ApiClient
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.SaveVehicleDetailResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class NavigationPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = NavigationPresenter::class.java.simpleName
    private var errorResponse = ErrorResponse()

    interface ResponseCallBack {

        fun onSuccess(responseModel: SaveVehicleDetailResponseModel)

        fun onFailure(errorResponse: ErrorResponse)

    }

    fun apiPostToLogoutUser(authToken: String) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.userLogout(Constants.App.AUTHORIZATION + authToken)

        call.enqueue(object : Callback<SaveVehicleDetailResponseModel> {
            override fun onResponse(
                call: Call<SaveVehicleDetailResponseModel>,
                responseModel: Response<SaveVehicleDetailResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    Log.d(TAG, "response  Body = ${responseModel.body()}")
                    responseCallBack.onSuccess(responseModel.body()!!)

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(TAG, "response failure Body = ${responseModel.errorBody()}")
                    responseCallBack.onFailure(errorResponse)
                }
            }

            override fun onFailure(
                call: Call<SaveVehicleDetailResponseModel>,
                t: Throwable
            ) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailure(errorResponse)
            }
        })
    }

}
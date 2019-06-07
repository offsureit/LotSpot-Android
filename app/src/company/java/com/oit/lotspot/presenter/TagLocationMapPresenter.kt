package com.oit.lotspot.presenter

import android.util.Log
import com.google.gson.Gson
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.interfaces.ApiInterface
import com.oit.lotspot.retrofit.ApiClient
import com.oit.lotspot.retrofit.request.SaveVehicleDetailsRequest
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.SaveVehicleDetailResponseModel
import com.oit.lotspot.retrofit.response.google.GoogleMapPathModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class TagLocationMapPresenter(var responseCallBack: ResponseCallBack) {

    private var TAG = TagLocationMapPresenter::class.java.simpleName
    private var errorResponse = ErrorResponse()

    interface ResponseCallBack {
        fun onSuccess(responseModel: SaveVehicleDetailResponseModel)
        fun onFailure(errorResponse: ErrorResponse)

        fun onSuccessRoute(response: GoogleMapPathModel)
        fun onFailureRoute()
    }

    fun saveVehicleDetails(authToken: String, saveDehicleDetailsRequest: SaveVehicleDetailsRequest) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.saveDetails(Constants.App.AUTHORIZATION + authToken, saveDehicleDetailsRequest)

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

    fun directionApiToGetRoute(origin: String, destination: String, sensor: String, mode: String, key: String) {
        val apiService = ApiClient.client2.create(ApiInterface::class.java)
        val call = apiService.getRoute(origin, destination, sensor, mode, key)

        call.enqueue(object : Callback<GoogleMapPathModel> {
            override fun onResponse(
                call: Call<GoogleMapPathModel>,
                responseModel: Response<GoogleMapPathModel>
            ) {

                if (responseModel.isSuccessful) {
                    Log.d(TAG, "response  Body = ${responseModel.body()}")
                    responseCallBack.onSuccessRoute(responseModel.body()!!)

                } else {
//                    val reader: Reader = responseModel.errorBody()!!.charStream()
//                    errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(TAG, "response failure Body = ${responseModel.errorBody()}")
                    responseCallBack.onFailureRoute()
                }
            }

            override fun onFailure(
                call: Call<GoogleMapPathModel>,
                t: Throwable
            ) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureRoute()
            }
        })
    }

}
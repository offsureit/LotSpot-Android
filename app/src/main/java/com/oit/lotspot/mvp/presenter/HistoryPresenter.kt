package com.oit.lotspot.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.interfaces.ApiInterface
import com.oit.lotspot.retrofit.ApiClient
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.VehicleDeleteResponse
import com.oit.lotspot.retrofit.response.VehicleListResponseModel

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class HistoryPresenter(var responseCallBack: ResponseCallBack) {

    private var TAG = HistoryPresenter::class.java.simpleName
    private var errorResponse = ErrorResponse()

    interface ResponseCallBack {

        fun onSuccessVehicleList(responseModel: VehicleListResponseModel)

        fun onFailureVehicleList(errorResponse: ErrorResponse)

        fun onSuccessVehicleDelete(response: VehicleDeleteResponse)

        fun onFailureVehicleDelete(errorResponse: ErrorResponse)
    }

    fun apiGetVehicleList(authToken: String) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.fetchVehicleList(
            Constants.App.AUTHORIZATION + authToken,
            1, 15
        )

        call.enqueue(object : Callback<VehicleListResponseModel> {
            override fun onResponse(
                call: Call<VehicleListResponseModel>,
                responseModel: Response<VehicleListResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    Log.d(TAG, "response  Body = ${responseModel.body()}")
                    responseCallBack.onSuccessVehicleList(responseModel.body()!!)

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(TAG, "response failure Body = ${responseModel.errorBody()}")
                    responseCallBack.onFailureVehicleList(errorResponse)
                }
            }

            override fun onFailure(
                call: Call<VehicleListResponseModel>,
                t: Throwable
            ) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureVehicleList(errorResponse)
            }
        })
    }

    fun apiToDeleteVehicleFromList(authToken: String, vehicleId: Int) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.deleteVehicle(Constants.App.AUTHORIZATION + authToken, vehicleId)

        call.enqueue(object : Callback<VehicleDeleteResponse> {
            override fun onResponse(
                call: Call<VehicleDeleteResponse>,
                responseModel: Response<VehicleDeleteResponse>
            ) {

                if (responseModel.isSuccessful) {
                    Log.d(TAG, "response  Body = ${responseModel.body()}")
                    responseCallBack.onSuccessVehicleDelete(responseModel.body()!!)

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(TAG, "response failure Body = ${responseModel.errorBody()}")
                    responseCallBack.onFailureVehicleDelete(errorResponse)
                }
            }

            override fun onFailure(
                call: Call<VehicleDeleteResponse>,
                t: Throwable
            ) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureVehicleDelete(errorResponse)
            }
        })
    }

    fun apiToDeleteAllVehicles(authToken: String) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.deleteAllVehicleList(Constants.App.AUTHORIZATION + authToken)

        call.enqueue(object : Callback<VehicleDeleteResponse> {
            override fun onResponse(
                call: Call<VehicleDeleteResponse>,
                responseModel: Response<VehicleDeleteResponse>
            ) {

                if (responseModel.isSuccessful) {
                    Log.d(TAG, "response  Body = ${responseModel.body()}")
                    responseCallBack.onSuccessVehicleDelete(responseModel.body()!!)

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(TAG, "response failure Body = ${responseModel.errorBody()}")
                    responseCallBack.onFailureVehicleDelete(errorResponse)
                }
            }

            override fun onFailure(
                call: Call<VehicleDeleteResponse>,
                t: Throwable
            ) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureVehicleDelete(errorResponse)
            }
        })
    }
}
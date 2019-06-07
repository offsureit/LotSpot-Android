package com.oit.lotspot.presenter

import android.util.Log
import com.oit.lotspot.interfaces.ApiInterface
import com.oit.lotspot.retrofit.ApiClient
import com.google.gson.Gson
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.retrofit.request.VehicleDetailRequest
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.LoginResponseModel
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class HomePresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = HomePresenter::class.java.simpleName
    private var errorResponse = ErrorResponse()

    interface ResponseCallBack {
        fun onSuccess(response: VehicleDetailResponseModel.VehicleDetailFirstResponseModel)

        fun onFailure(errorResponse: ErrorResponse)

        fun onSuccessProfile(response: LoginResponseModel.LoginResponseProfileModel)
    }

    fun apiGetForVehicleDetails(token: String, vehicleDetailRequest: VehicleDetailRequest) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getVehicleDetails(Constants.App.AUTHORIZATION + token, vehicleDetailRequest)

        call.enqueue(object : Callback<VehicleDetailResponseModel.VehicleDetailFirstResponseModel> {
            override fun onResponse(
                call: Call<VehicleDetailResponseModel.VehicleDetailFirstResponseModel>,
                responseModel: Response<VehicleDetailResponseModel.VehicleDetailFirstResponseModel>
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
                call: Call<VehicleDetailResponseModel.VehicleDetailFirstResponseModel>,
                t: Throwable
            ) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailure(errorResponse)
            }
        })
    }

    fun apiGetForCompanyProfile(authToken: String) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.companyProfile(Constants.App.AUTHORIZATION + authToken)

        call.enqueue(object : Callback<LoginResponseModel.LoginResponseProfileModel> {
            override fun onResponse(
                call: Call<LoginResponseModel.LoginResponseProfileModel>,
                responseModel: Response<LoginResponseModel.LoginResponseProfileModel>
            ) {

                if (responseModel.isSuccessful) {
                    Log.d(TAG, "response  Body = ${responseModel.body()}")
                    responseCallBack.onSuccessProfile(responseModel.body()!!)

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(TAG, "response failure Body = ${responseModel.errorBody()}")
                    responseCallBack.onFailure(errorResponse)
                }
            }

            override fun onFailure(
                call: Call<LoginResponseModel.LoginResponseProfileModel>,
                t: Throwable
            ) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailure(errorResponse)
            }
        })
    }
}
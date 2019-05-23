package com.oit.lotspot.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.oit.lotspot.interfaces.ApiInterface
import com.oit.lotspot.retrofit.ApiClient
import com.oit.lotspot.retrofit.request.LoginRequestModel
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.LoginResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class LoginPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = LoginPresenter::class.java.simpleName
    private var errorResponse = ErrorResponse()

    interface ResponseCallBack {

        fun onSuccess(responseLogin: LoginResponseModel.LoginResponseFirstModel)

        fun onFailure(errorResponse: ErrorResponse)

    }

    fun apiPostForLogin(loginRequestModel: LoginRequestModel) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.userLogin(loginRequestModel)

        call.enqueue(object : Callback<LoginResponseModel.LoginResponseFirstModel> {
            override fun onResponse(
                call: Call<LoginResponseModel.LoginResponseFirstModel>,
                responseModelLogin: Response<LoginResponseModel.LoginResponseFirstModel>
            ) {

                if (responseModelLogin.isSuccessful) {
                    Log.d(TAG, "response login for Body = ${responseModelLogin.body()}")
                    responseCallBack.onSuccess(responseModelLogin.body()!!)

                } else {
                    val reader: Reader = responseModelLogin.errorBody()!!.charStream()
                    errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(TAG, "response for error Body = ${responseModelLogin.errorBody()}")
                    responseCallBack.onFailure(errorResponse)
                }
            }

            override fun onFailure(call: Call<LoginResponseModel.LoginResponseFirstModel>, t: Throwable) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailure(errorResponse)
            }
        })
    }

}
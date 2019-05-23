package com.oit.lotspot.interfaces

import com.oit.lotspot.retrofit.request.LoginRequestModel
import com.oit.lotspot.retrofit.request.SaveVehicleDetailsRequest
import com.oit.lotspot.retrofit.request.VehicleDetailRequest
import com.oit.lotspot.retrofit.response.*
import com.oit.lotspot.retrofit.response.google.GoogleMapPathModel
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("api/auth/company/login")
    fun userLogin(@Body requestModel: LoginRequestModel): Call<LoginResponseModel.LoginResponseFirstModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("api/vehicle/car-md")
    fun getVehicleDetails(@Header("Authorization") authToken: String, @Body vehicleDetailRequest: VehicleDetailRequest):
            Call<VehicleDetailResponseModel.VehicleDetailFirstResponseModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("api/vehicle/save")
    fun saveDetails(@Header("Authorization") authToken: String, @Body saveVehicleDetailsRequest: SaveVehicleDetailsRequest):
            Call<SaveVehicleDetailResponseModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("api/vehicle")
    fun fetchVehicleList(
        @Header("Authorization") authToken: String, @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<VehicleListResponseModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @DELETE("api/vehicle/{id}")
    fun deleteVehicle(@Header("Authorization") authToken: String, @Path("id") id: Int):
            Call<VehicleDeleteResponse>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @DELETE("api/vehicle")
    fun deleteAllVehicleList(@Header("Authorization") authToken: String):
            Call<VehicleDeleteResponse>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @POST("api/auth/company/logout")
    fun userLogout(@Header("Authorization") authToken: String):
            Call<SaveVehicleDetailResponseModel>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("directions/json")
    fun getRoute(
        @Query("origin") origin: String, @Query("destination") destination: String,
        @Query("sensor") sensor: String, @Query("mode") mode: String, @Query("key") key: String
    ): Call<GoogleMapPathModel>
}
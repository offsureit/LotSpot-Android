package com.oit.lotspot.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val BASE_URL = "http://103.36.77.34/lotspot/public/"

    val BASE_URL_LIVE = "http://103.212.235.82/lotspot/"

    val BASE_URL_LINKS = "http://103.36.77.34/lotspot/"

    val DIRECTIONS_API_LINK = "https://maps.googleapis.com/maps/api/"

    val BASE_URL_SIGNUP = "http://103.212.235.82/lotspot/site/#/user/login"

    private var retrofit: Retrofit? = null
    private var retrofit2: Retrofit? = null
   // private var retrofit_live: Retrofit? = null

    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL_LIVE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

    val client2: Retrofit
        get() {
            if (retrofit2 == null) {
                retrofit2 = Retrofit.Builder()
                    .baseUrl(DIRECTIONS_API_LINK)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit2!!
        }

//    val client_live: Retrofit
//        get() {
//            if (retrofit_live == null) {
//                retrofit_live = Retrofit.Builder()
//                    .baseUrl(BASE_URL_LIVE)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//            }
//            return retrofit_live!!
//        }

}


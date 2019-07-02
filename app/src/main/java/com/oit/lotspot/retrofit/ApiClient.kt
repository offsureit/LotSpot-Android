package com.oit.lotspot.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val BASE_URL = "http://lotspotterapp.com/"

    val BASE_URL_LIVE = BASE_URL + "backend/public/"

    val BASE_URL_PRIVACY = "$BASE_URL#/privacy-policy"

    val BASE_URL_TERMS = "$BASE_URL#/terms-conditions"

    val DIRECTIONS_API_LINK = "https://maps.googleapis.com/maps/api/"

    val BASE_URL_SIGNUP = "$BASE_URL#/user/signup"

    private var retrofit: Retrofit? = null
    private var retrofit2: Retrofit? = null

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
}


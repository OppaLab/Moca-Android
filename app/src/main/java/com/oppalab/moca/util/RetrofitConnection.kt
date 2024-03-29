package com.oppalab.moca.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConnection {
    val URL = "http://49.50.160.123:8080"
    val retrofit = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val server = retrofit.create(RetrofitService::class.java)
}
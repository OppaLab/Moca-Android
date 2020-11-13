package com.oppalab.moca.util

import com.oppalab.moca.`interface`.RetrofitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {
    val URL = "localhost:8080"
    val retrofit = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val server = retrofit.create(RetrofitService::class.java)
}
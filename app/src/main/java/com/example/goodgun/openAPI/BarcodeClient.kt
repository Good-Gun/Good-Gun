package com.example.goodgun.openAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BarcodeClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://openapi.foodsafetykorea.go.kr")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service by lazy { retrofit.create(BarcodeInterface::class.java) }
}

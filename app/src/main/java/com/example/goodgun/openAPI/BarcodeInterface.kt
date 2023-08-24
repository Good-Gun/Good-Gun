package com.example.goodgun.openAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BarcodeInterface {

    @GET("api/{keyId}/C005/json/1/1/BAR_CD={BAR_CD}")
    fun getBarcodeInfo(
        @Path("keyId") keyId: String,
        @Path("BAR_CD") BAR_CD: String,
    ): Call<BarcodeList>
}

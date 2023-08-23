package com.example.goodgun.openAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BarcodeInterface {

    @GET("api/{keyId}/I2570/json/1/1/BRCD_NO={BRCD_NO}")
    fun getBarcodeInfo(
        @Path("keyId") keyId: String,
        @Path("BRCD_NO") BRCD_NO: String,
    ): Call<BarcodeList>
}
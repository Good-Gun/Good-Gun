package com.example.goodgun.openAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodService {
    @GET("api/{keyId}/{seviceId}/{dataType}/1/1000")
    fun getFoodName(
        @Path("keyId") keyId: String,
        @Path("serviceId") serviceId: String,
        @Path("dataType") dataType: String,
    ): Call<FoodList>
}

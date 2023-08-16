package com.example.goodgun.openAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodService {
    @GET("api/{keyId}/{serviceId}/{dataType}/1/100")
    fun getFoodName(
        @Path("keyId") keyId : String,
        @Path("serviceId") serviceId : String,
        @Path("dataType") dataType : String,
    ):Call<FoodList>
}
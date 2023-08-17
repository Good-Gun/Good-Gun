package com.example.goodgun.openAPI

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("api/login")
    fun login(@Body request: LoginData): Call<Void>
}

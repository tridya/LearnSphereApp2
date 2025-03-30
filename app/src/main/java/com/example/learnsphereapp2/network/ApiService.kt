package com.example.learnsphereapp2.network

import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Token>

    @GET("api/users/me")
    suspend fun getUser(@Header("Authorization") authorization: String): Response<UserResponse>

    @GET("/")
    suspend fun checkHealth(): Map<String, String>
}
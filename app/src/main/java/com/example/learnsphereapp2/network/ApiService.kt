package com.example.learnsphereapp2.network

import com.example.learnsphereapp2.data.model.Absensi
import com.example.learnsphereapp2.data.model.AbsensiRequest
import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

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

    @POST("api/absensi/")
    suspend fun createAbsensi(
        @Header("Authorization") authorization: String,
        @Body absensi: AbsensiRequest
    ): Response<Absensi>

    @GET("api/absensi/kelas/{kelas_id}")
    suspend fun getAbsensiByKelas(
        @Header("Authorization") authorization: String,
        @Path("kelas_id") kelasId: Int,
        @Query("tanggal") tanggal: String? = null
    ): Response<List<Absensi>>
}
package com.example.learnsphereapp2.network

import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import com.example.learnsphereapp2.data.model.AbsensiCreate
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.KelasResponse
import com.example.learnsphereapp2.data.model.SiswaDetailResponse
import com.example.learnsphereapp2.data.model.SiswaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("api/siswa/kelas/{kelas_id}")
    suspend fun getStudentsByClass(
        @Header("Authorization") authorization: String,
        @Path("kelas_id") kelasId: Int
    ): Response<List<SiswaResponse>>

    @POST("api/absensi")
    suspend fun createAbsensi(
        @Header("Authorization") authorization: String,
        @Body absensi: AbsensiCreate
    ): Response<AbsensiResponse>

    @GET("api/absensi/kelas/{kelas_id}")
    suspend fun getAbsensiByClassAndDate(
        @Header("Authorization") authorization: String,
        @Path("kelas_id") kelasId: Int,
        @Query("tanggal") tanggal: String
    ): Response<List<AbsensiResponse>>

    @GET("api/kelas/")
    suspend fun getKelasForGuru(
        @Header("Authorization") token: String
    ): Response<List<KelasResponse>>

    @GET("api/siswa/by-kode/{kode_siswa}")
    suspend fun getSiswaByKode(
        @Header("Authorization") authorization: String,
        @Path("kode_siswa") kodeSiswa: String
    ): Response<SiswaDetailResponse>

    @GET("api/siswa/orang-tua")
    suspend fun getSiswaByOrangTua(
        @Header("Authorization") authorization: String
    ): Response<List<SiswaResponse>>
}
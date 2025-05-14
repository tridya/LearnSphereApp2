package com.example.learnsphereapp2.network

import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import com.example.learnsphereapp2.data.model.AbsensiCreate
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.JadwalCreate
import com.example.learnsphereapp2.data.model.JadwalResponse
import com.example.learnsphereapp2.data.model.KelasResponse
import com.example.learnsphereapp2.data.model.MataPelajaranResponse
import com.example.learnsphereapp2.data.model.RekapanSiswaCreate
import com.example.learnsphereapp2.data.model.RekapanSiswaResponse
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.data.model.StatusRekapanSiswa
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("api/absensi/siswa/{siswa_id}")
    suspend fun getAbsensiByStudent(
        @Header("Authorization") authorization: String,
        @Path("siswa_id") siswaId: Int,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<AbsensiResponse>>

    @GET("api/kelas/guru")
    suspend fun getKelasByGuru(
        @Header("Authorization") authorization: String
    ): Response<List<KelasResponse>>

    @GET("api/jadwal/guru/kelas")
    suspend fun getKelasByTeacher(
        @Header("Authorization") authorization: String
    ): Response<List<KelasResponse>>

    @POST("api/jadwal")
    suspend fun createJadwal(
        @Header("Authorization") authorization: String,
        @Body jadwal: JadwalCreate
    ): Response<JadwalResponse>

    @DELETE("api/jadwal/{jadwal_id}")
    suspend fun deleteJadwal(
        @Header("Authorization") authorization: String,
        @Path("jadwal_id") jadwalId: Int
    ): Response<Map<String, String>>

    @PUT("api/jadwal/{jadwal_id}")
    suspend fun updateJadwal(
        @Header("Authorization") authorization: String,
        @Path("jadwal_id") jadwalId: Int,
        @Body jadwal: JadwalCreate
    ): Response<JadwalResponse>

    @GET("api/jadwal/kelas/{kelas_id}/current")
    suspend fun getCurrentJadwalByKelas(
        @Header("Authorization") authorization: String,
        @Path("kelas_id") kelasId: Int
    ): Response<List<JadwalResponse>>

    @GET("api/jadwal/kelas/{kelas_id}")
    suspend fun getAllJadwalByKelas(
        @Header("Authorization") authorization: String,
        @Path("kelas_id") kelasId: Int
    ): Response<List<JadwalResponse>>

    @GET("api/rekapan-siswa/rekapan/kelas/{kelasId}/mata_pelajaran/{mataPelajaranId}")
    suspend fun getRekapanByKelas(
        @Path("kelasId") kelasId: Int,
        @Path("mataPelajaranId") mataPelajaranId: Int,
        @Header("Authorization") token: String
    ): List<StatusRekapanSiswa>

    @GET("api/rekapan-siswa/jadwal/kelas/{kelasId}")
    suspend fun getJadwalByKelas(
        @Path("kelasId") kelasId: Int,
        @Header("Authorization") token: String
    ): List<JadwalResponse>


    @GET("api/rekapan-siswa/mata_pelajaran")
    suspend fun getMataPelajaran(
        @Header("Authorization") token: String
    ): List<MataPelajaranResponse>

    @GET("api/rekapan-siswa/kelas")
    suspend fun getKelas(
        @Header("Authorization") token: String
    ): List<KelasResponse>

    // In ApiService.kt
    @GET("api/rekapan-siswa/daily/{kelasId}")
    suspend fun getDailyRekapan(
        @Path("kelasId") kelasId: Int,
        @Query("tanggal") tanggal: String, // Format: YYYY-MM-DD
        @Header("Authorization") token: String
    ): List<RekapanSiswaResponse>


    @POST("api/rekapan-siswa/daily")
    suspend fun createDailyRekapan(
        @Header("Authorization") token: String,
        @Body rekapan: RekapanSiswaCreate
    ): RekapanSiswaResponse
}
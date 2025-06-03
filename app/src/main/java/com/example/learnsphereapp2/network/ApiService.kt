package com.example.learnsphereapp2.network

import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import com.example.learnsphereapp2.data.model.AbsensiCreate
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.JadwalCreate
import com.example.learnsphereapp2.data.model.JadwalResponse
import com.example.learnsphereapp2.data.model.Holiday
import com.example.learnsphereapp2.data.model.KelasResponse
import com.example.learnsphereapp2.data.model.SiswaResponse
import retrofit2.Response
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    // Endpoint untuk API hari libur nasional dari libur.deno.dev
    @GET("https://libur.deno.dev/api")
    suspend fun getNationalHolidays(
        @Query("year") year: Int
    ): Response<List<Holiday>>

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

    @Multipart
    @POST("api/users/me/profile-picture")
    suspend fun uploadProfilePicture(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @GET("api/jadwal/orangtua/siswa/{siswa_id}")
    suspend fun getJadwalBySiswa(
        @Header("Authorization") authorization: String,
        @Path("siswa_id") siswaId: Int
    ): Response<List<JadwalResponse>>

    @GET("api/jadwal/orangtua/siswa/{siswa_id}/current")
    suspend fun getCurrentJadwalBySiswa(
        @Header("Authorization") authorization: String,
        @Path("siswa_id") siswaId: Int
    ): Response<List<JadwalResponse>>

    @GET("api/siswa/orangtua")
    suspend fun getSiswaByParent(
        @Header("Authorization") authorization: String
    ): Response<List<SiswaResponse>>
}
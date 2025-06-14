package com.example.learnsphereapp2.network

import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import com.example.learnsphereapp2.data.model.AbsensiCreate
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.JadwalCreate
import com.example.learnsphereapp2.data.model.JadwalResponse
import com.example.learnsphereapp2.data.model.Holiday
import com.example.learnsphereapp2.data.model.KelasResponse
import com.example.learnsphereapp2.data.model.MataPelajaranResponse
import com.example.learnsphereapp2.data.model.RekapanSiswaCreate
import com.example.learnsphereapp2.data.model.RekapanSiswaResponse
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.data.model.StatusRekapanSiswa
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

    @GET("api/rekapan-siswa/daily/{kelasId}")
    suspend fun getDailyRekapan(
        @Path("kelasId") kelasId: Int,
        @Query("tanggal") tanggal: String,
        @Header("Authorization") token: String
    ): List<RekapanSiswaResponse>

    @POST("api/rekapan-siswa/daily")
    suspend fun createDailyRekapan(
        @Header("Authorization") token: String,
        @Body rekapan: RekapanSiswaCreate
    ): RekapanSiswaResponse

    @GET("api/rekapan-siswa/siswa")
    suspend fun getSiswaOrangTua(
        @Header("Authorization") token: String
    ): List<SiswaResponse>

    @GET("api/rekapan-siswa/orangtua/{siswa_id}/jadwal")
    suspend fun getAllJadwalByKelas(
        @Header("Authorization") token: String,
        @Path("siswa_id") siswaId: Int,
        @Query("hari") hari: String? = null
    ): List<JadwalResponse>

    @GET("api/rekapan-siswa/rekapan")
    suspend fun getRekapanSiswaOrangTua(
        @Header("Authorization") token: String,
        @Query("siswa_id") siswaId: Int,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("mata_pelajaran_id") mataPelajaranId: Int? = null
    ): List<RekapanSiswaResponse>

    @GET("api/rekapan-siswa/siswa/{siswa_id}")
    suspend fun getSiswaById(
        @Header("Authorization") token: String,
        @Path("siswa_id") siswaId: Int
    ): SiswaResponse

    @GET("api/rekapan-siswa/orangtua/{siswa_id}")
    suspend fun getRekapanBySiswaId(
        @Header("Authorization") token: String,
        @Path("siswa_id") siswaId: Int,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("mata_pelajaran_id") mataPelajaranId: Int? = null
    ): List<RekapanSiswaResponse>

    @GET("api/rekapan-siswa/orangtua/{siswa_id}/jadwal")
    suspend fun getAllJadwalBySiswa(
        @Header("Authorization") token: String,
        @Path("siswa_id") siswaId: Int,
        @Query("hari") hari: String? = null
    ): List<JadwalResponse>

    @GET("api/jadwal/orangtua/siswa/{siswa_id}/current")
    suspend fun getCurrentJadwalBySiswa(
        @Header("Authorization") authorization: String,
        @Path("siswa_id") siswaId: Int
    ): Response<List<JadwalResponse>>

    @GET("api/jadwal/orangtua/siswa/{siswa_id}")
    suspend fun getJadwalBySiswa(
        @Header("Authorization") authorization: String,
        @Path("siswa_id") siswaId: Int
    ): Response<List<JadwalResponse>>

    @GET("https://libur.deno.dev/api")
    suspend fun getNationalHolidays(
        @Query("year") year: Int
    ): Response<List<Holiday>>

    @Multipart
    @POST("api/users/me/profile-picture")
    suspend fun uploadProfilePicture(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @GET("api/siswa/orangtua")
    suspend fun getSiswaByParent(
        @Header("Authorization") authorization: String
    ): Response<List<SiswaResponse>>


}
package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class JadwalResponse(
    @SerializedName("jadwal_id") val jadwalId: Int,
    @SerializedName("kelas_id") val kelasId: Int,
    val hari: String,
    @SerializedName("jam_mulai") val jamMulai: String,
    @SerializedName("jam_selesai") val jamSelesai: String,
    @SerializedName("mata_pelajaran_id") val mataPelajaranId: Int,
    @SerializedName("mata_pelajaran") val mataPelajaran: MataPelajaranResponse,
    @SerializedName("wali_kelas") val waliKelas: UserResponse
)

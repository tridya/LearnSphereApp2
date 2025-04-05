package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class Absensi(
    @SerializedName("absensi_id")
    val absensiId: Int,
    @SerializedName("siswa_id")
    val siswaId: Int,
    @SerializedName("kelas_id")
    val kelasId: Int,
    @SerializedName("tanggal")
    val tanggal: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("catatan")
    val catatan: String?,
    @SerializedName("created_at")
    val createdAt: String
)


package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class AbsensiRequest(
    @SerializedName("siswa_id")
    val siswaId: Int,
    @SerializedName("kelas_id")
    val kelasId: Int,
    @SerializedName("tanggal")
    val tanggal: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("catatan")
    val catatan: String? = null
)
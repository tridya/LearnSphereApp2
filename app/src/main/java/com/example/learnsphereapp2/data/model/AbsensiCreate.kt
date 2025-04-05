package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class AbsensiCreate(
    @SerializedName("siswa_id")
    val siswaId: Int,
    @SerializedName("tanggal")
    val tanggal: String,
    @SerializedName("status")
    val status: String
)
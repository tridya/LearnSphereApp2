package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class KelasResponse(
    @SerializedName("kelas_id")
    val kelasId: Int,
    @SerializedName("nama_kelas")
    val namaKelas: String
)
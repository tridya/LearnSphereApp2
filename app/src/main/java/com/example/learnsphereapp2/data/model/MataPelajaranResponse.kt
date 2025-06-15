package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class MataPelajaranResponse(
    @SerializedName("mata_pelajaran_id") val mataPelajaranId: Int,
    val nama: String,
    val kode: String?,
    val deskripsi: String?
)
package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class SiswaDetailResponse(
    @SerializedName("siswa")
    val siswa: SiswaResponse,
    @SerializedName("absensi")
    val absensi: List<AbsensiResponse>
)
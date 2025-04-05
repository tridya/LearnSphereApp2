package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class SiswaResponse(
    @SerializedName("siswa_id")
    val siswaId: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("kelas_id")
    val kelasId: Int,
    @SerializedName("orang_tua_id")
    val orangTuaId: Int?,
    @SerializedName("kode_siswa")
    val kodeSiswa: String?
)
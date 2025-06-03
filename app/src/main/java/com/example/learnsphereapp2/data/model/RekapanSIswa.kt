package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class RekapanSiswaCreate(
    @SerializedName("siswa_id") val siswa_id: Int,
    @SerializedName("guru_id") val guru_id: Int,
    @SerializedName("mata_pelajaran_id") val mata_pelajaran_id: Int,
    @SerializedName("rating") val rating: String,
    @SerializedName("catatan") val catatan: String?,

    )

data class RekapanSiswaResponse(
    @SerializedName("report_id") val reportId: Int,
    @SerializedName("siswa_id") val siswaId: Int,
    @SerializedName("guru_id") val guruId: Int,
    @SerializedName("mata_pelajaran_id") val mataPelajaranId: Int,
    @SerializedName("mata_pelajaran") val mataPelajaran: MataPelajaranResponse? = null,
    @SerializedName("nama_siswa") val namaSiswa: String? = null,
    val rating: String,
    val catatan: String?,
    val tanggal: String


)

data class StatusRekapanSiswa(
    @SerializedName("siswa_id") val siswaId: Int,
    @SerializedName("nama_siswa") val namaSiswa: String,
    @SerializedName("sudah_dibuat") val sudahDibuat: Boolean,
    val rekapan: RekapanSiswaResponse?
)
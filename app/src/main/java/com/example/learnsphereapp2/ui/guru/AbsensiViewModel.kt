package com.example.learnsphereapp2.ui.guru

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.AbsensiCreate
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AbsensiViewModel(
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {
    val siswaList = mutableStateOf<List<SiswaResponse>>(emptyList())
    val absensiList = mutableStateOf<List<AbsensiResponse>>(emptyList())
    val statusMap = mutableStateMapOf<Int, String>()
    val hadirCount = mutableStateOf(0)
    val absenCount = mutableStateOf(0)
    val izinCount = mutableStateOf(0)
    val sakitCount = mutableStateOf(0)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private val formatterApi = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun fetchData(kelasId: Int, tanggal: LocalDate?) {
        if (tanggal == null) {
            errorMessage.value = "Tanggal tidak valid."
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    return@launch
                }
                Log.d("AbsensiViewModel", "Token: $token")

                // Ambil daftar siswa
                try {
                    Log.d("AbsensiViewModel", "Mengambil daftar siswa untuk kelasId: $kelasId")
                    val siswaResponse = RetrofitClient.apiService.getStudentsByClass(
                        authorization = "Bearer $token",
                        kelasId = kelasId
                    )
                    if (siswaResponse.isSuccessful) {
                        siswaList.value = siswaResponse.body() ?: emptyList()
                        siswaList.value.forEach { siswa ->
                            if (!statusMap.containsKey(siswa.siswaId)) {
                                statusMap[siswa.siswaId] = "Belum Diisi"
                            }
                        }
                        Log.d("AbsensiViewModel", "Berhasil mengambil ${siswaList.value.size} siswa")
                    } else {
                        errorMessage.value = when (siswaResponse.code()) {
                            404 -> "Siswa tidak ditemukan untuk kelas ini."
                            403 -> "Anda tidak memiliki akses ke kelas ini."
                            else -> "Gagal mengambil daftar siswa: ${siswaResponse.message()}"
                        }
                        Log.e("AbsensiViewModel", "Gagal mengambil siswa: ${siswaResponse.code()}")
                    }
                } catch (e: Exception) {
                    errorMessage.value = "Error saat mengambil daftar siswa: ${e.message}"
                    Log.e("AbsensiViewModel", "Error mengambil siswa: ${e.message}", e)
                }

                // Ambil absensi untuk tanggal yang dipilih
                try {
                    val tanggalApi = tanggal.format(formatterApi)
                    Log.d("AbsensiViewModel", "Mengambil absensi untuk kelasId: $kelasId, tanggal: $tanggalApi")
                    val absensiResponse = RetrofitClient.apiService.getAbsensiByClassAndDate(
                        authorization = "Bearer $token",
                        kelasId = kelasId,
                        tanggal = tanggalApi
                    )
                    if (absensiResponse.isSuccessful) {
                        absensiList.value = absensiResponse.body() ?: emptyList()
                        absensiList.value.forEach { absensi ->
                            statusMap[absensi.siswaId] = absensi.status
                        }
                        updateStatistics()
                        Log.d("AbsensiViewModel", "Berhasil mengambil ${absensiList.value.size} data absensi")
                    } else {
                        errorMessage.value = when (absensiResponse.code()) {
                            404 -> "Absensi tidak ditemukan untuk tanggal ini."
                            403 -> "Anda tidak memiliki akses ke kelas ini."
                            else -> "Gagal mengambil absensi: ${absensiResponse.message()}"
                        }
                        Log.e("AbsensiViewModel", "Gagal mengambil absensi: ${absensiResponse.code()}")
                    }
                } catch (e: Exception) {
                    errorMessage.value = "Error saat mengambil absensi: ${e.message}"
                    Log.e("AbsensiViewModel", "Error mengambil absensi: ${e.message}", e)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil data: ${e.message}"
                Log.e("AbsensiViewModel", "Error umum: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateAbsensi(siswaId: Int, tanggal: String, newStatus: String) {
        viewModelScope.launch {
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    return@launch
                }
                val absensi = AbsensiCreate(
                    siswaId = siswaId,
                    tanggal = tanggal,
                    status = newStatus
                )
                Log.d("AbsensiViewModel", "Mengupdate absensi untuk siswaId: $siswaId, tanggal: $tanggal, status: $newStatus")
                val response = RetrofitClient.apiService.createAbsensi(
                    authorization = "Bearer $token",
                    absensi = absensi
                )
                if (response.isSuccessful) {
                    val updatedAbsensi = absensiList.value.toMutableList()
                    val existingAbsensiIndex = updatedAbsensi.indexOfFirst { it.siswaId == siswaId }
                    if (existingAbsensiIndex != -1) {
                        response.body()?.let { updatedAbsensi[existingAbsensiIndex] = it }
                    } else {
                        response.body()?.let { updatedAbsensi.add(it) }
                    }
                    absensiList.value = updatedAbsensi
                    statusMap[siswaId] = newStatus
                    updateStatistics()
                    Log.d("AbsensiViewModel", "Berhasil mengupdate absensi untuk siswaId: $siswaId")
                } else {
                    errorMessage.value = when (response.code()) {
                        404 -> "Siswa tidak ditemukan."
                        403 -> "Anda tidak memiliki akses untuk mengubah absensi."
                        else -> "Gagal mengupdate absensi: ${response.message()}"
                    }
                    Log.e("AbsensiViewModel", "Gagal mengupdate absensi: ${response.code()}")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengupdate absensi: ${e.message}"
                Log.e("AbsensiViewModel", "Error mengupdate absensi: ${e.message}", e)
            }
        }
    }

    private fun updateStatistics() {
        hadirCount.value = absensiList.value.count { it.status == "Hadir" }
        absenCount.value = absensiList.value.count { it.status == "Alpa" }
        izinCount.value = absensiList.value.count { it.status == "Izin" }
        sakitCount.value = absensiList.value.count { it.status == "Sakit" }
    }
}
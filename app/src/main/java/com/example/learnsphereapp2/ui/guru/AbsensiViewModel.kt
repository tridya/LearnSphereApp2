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
import java.time.format.DateTimeParseException

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

    fun fetchData(kelasId: Int, tanggal: LocalDate) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            statusMap.clear() // Reset statusMap untuk data baru
            Log.d("AbsensiViewModel", "Fetching data for kelasId: $kelasId, tanggal: $tanggal")

            val token = preferencesHelper.getToken() ?: run {
                errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                isLoading.value = false
                Log.e("AbsensiViewModel", "Token not found")
                return@launch
            }

            // Ambil daftar siswa
            try {
                Log.d("AbsensiViewModel", "Fetching siswa for kelasId: $kelasId")
                val siswaResponse = RetrofitClient.apiService.getStudentsByClass(
                    authorization = "Bearer $token",
                    kelasId = kelasId
                )
                if (siswaResponse.isSuccessful) {
                    siswaList.value = siswaResponse.body() ?: emptyList()
                    siswaList.value.forEach { statusMap.putIfAbsent(it.siswaId, "Hadir") }
                    Log.d("AbsensiViewModel", "Siswa fetched: ${siswaList.value.size} for kelasId: $kelasId")
                } else {
                    val errorBody = siswaResponse.errorBody()?.string()
                    errorMessage.value = "Gagal mengambil data siswa: ${siswaResponse.code()} - $errorBody"
                    Log.e("AbsensiViewModel", "Siswa fetch failed for kelasId: $kelasId: ${siswaResponse.code()} - $errorBody")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil data siswa: ${e.message}"
                Log.e("AbsensiViewModel", "Siswa fetch error for kelasId: $kelasId", e)
            }

            // Ambil absensi
            try {
                val tanggalApi = tanggal.format(formatterApi)
                Log.d("AbsensiViewModel", "Fetching absensi for kelasId: $kelasId, tanggal: $tanggalApi")
                val absensiResponse = RetrofitClient.apiService.getAbsensiByClassAndDate(
                    authorization = "Bearer $token",
                    kelasId = kelasId,
                    tanggal = tanggalApi
                )
                if (absensiResponse.isSuccessful) {
                    absensiList.value = absensiResponse.body() ?: emptyList()
                    absensiList.value.forEach { statusMap[it.siswaId] = it.status }
                    updateStatistics()
                    Log.d("AbsensiViewModel", "Absensi fetched: ${absensiList.value.size} for kelasId: $kelasId")
                } else {
                    val errorBody = absensiResponse.errorBody()?.string()
                    errorMessage.value = "Gagal mengambil data absensi: ${absensiResponse.code()} - $errorBody"
                    Log.e("AbsensiViewModel", "Absensi fetch failed for kelasId: $kelasId: ${absensiResponse.code()} - $errorBody")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil data absensi: ${e.message}"
                Log.e("AbsensiViewModel", "Absensi fetch error for kelasId: $kelasId", e)
            } finally {
                updateStatistics() // Pastikan statistik selalu diperbarui
                isLoading.value = false
            }
        }
    }

    fun updateAbsensi(siswaId: Int, tanggal: String, newStatus: String) {
        viewModelScope.launch {
            val token = preferencesHelper.getToken() ?: run {
                errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                Log.e("AbsensiViewModel", "Token not found during update")
                return@launch
            }

            // Validasi format tanggal
            val validatedTanggal = try {
                LocalDate.parse(tanggal, formatterApi)
                tanggal
            } catch (e: DateTimeParseException) {
                errorMessage.value = "Format tanggal tidak valid: $tanggal. Harus yyyy-MM-dd."
                Log.e("AbsensiViewModel", "Invalid date format: $tanggal", e)
                return@launch
            }

            try {
                Log.d("AbsensiViewModel", "Updating absensi for siswaId: $siswaId, tanggal: $validatedTanggal, status: $newStatus")
                val absensi = AbsensiCreate(siswaId = siswaId, tanggal = validatedTanggal, status = newStatus)
                val response = RetrofitClient.apiService.createAbsensi("Bearer $token", absensi)
                if (response.isSuccessful) {
                    val updatedAbsensi = response.body()!!
                    val currentList = absensiList.value.toMutableList()
                    val index = currentList.indexOfFirst { it.siswaId == siswaId }
                    if (index != -1) {
                        currentList[index] = updatedAbsensi
                    } else {
                        currentList.add(updatedAbsensi)
                    }
                    absensiList.value = currentList
                    statusMap[siswaId] = newStatus
                    updateStatistics()
                    Log.d("AbsensiViewModel", "Absensi updated successfully for siswaId: $siswaId")
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage.value = "Gagal update absensi: ${response.code()} - $errorBody"
                    Log.e("AbsensiViewModel", "Update failed for siswaId: $siswaId: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat update absensi: ${e.message}"
                Log.e("AbsensiViewModel", "Update error for siswaId: $siswaId", e)
            }
        }
    }

    private fun updateStatistics() {
        hadirCount.value = absensiList.value.count { it.status == "Hadir" }
        absenCount.value = absensiList.value.count { it.status == "Alpa" }
        izinCount.value = absensiList.value.count { it.status == "Izin" }
        sakitCount.value = absensiList.value.count { it.status == "Sakit" }
        Log.d("AbsensiViewModel", "Statistics updated: Hadir=${hadirCount.value}, Alpa=${absenCount.value}, Izin=${izinCount.value}, Sakit=${sakitCount.value}")
    }
}
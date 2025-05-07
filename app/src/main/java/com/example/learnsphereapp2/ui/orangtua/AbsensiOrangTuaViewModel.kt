package com.example.learnsphereapp2.ui.orangtua

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AbsensiOrangTuaViewModel(
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {
    val absensiList = mutableStateOf<List<AbsensiResponse>>(emptyList())
    val hadirCount = mutableStateOf(0)
    val absenCount = mutableStateOf(0)
    val izinCount = mutableStateOf(0)
    val sakitCount = mutableStateOf(0)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private val formatterApi = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale("id", "ID"))

    fun fetchAbsensiByStudent(date: LocalDate, siswaId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                Log.d("AbsensiOrangTuaVM", "Menggunakan siswa_id: $siswaId")

                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    Log.e("AbsensiOrangTuaVM", "Token tidak ditemukan di PreferencesHelper")
                    return@launch
                }
                Log.d("AbsensiOrangTuaVM", "Token: $token")

                val startDate = date.withDayOfMonth(1).format(formatterApi)
                val endDate = date.withDayOfMonth(date.lengthOfMonth()).format(formatterApi)
                Log.d("AbsensiOrangTuaVM", "Parameter: siswa_id=$siswaId, start_date=$startDate, end_date=$endDate")

                val response = RetrofitClient.apiService.getAbsensiByStudent(
                    authorization = "Bearer $token",
                    siswaId = siswaId,
                    startDate = startDate,
                    endDate = endDate
                )
                Log.d("AbsensiOrangTuaVM", "Respon kode: ${response.code()}")
                val rawBody = response.errorBody()?.string() ?: response.body()?.toString() ?: "null"
                Log.d("AbsensiOrangTuaVM", "Respon body mentah: $rawBody")

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    absensiList.value = body
                    updateStatistics()
                    Log.d("AbsensiOrangTuaVM", "Berhasil mengambil ${absensiList.value.size} data absensi: ${absensiList.value.map { "${it.tanggal}: ${it.status}" }}")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Tidak ada detail error"
                    errorMessage.value = when (response.code()) {
                        404 -> "Absensi tidak ditemukan untuk periode ini."
                        403 -> "Anda tidak memiliki akses."
                        else -> "Gagal mengambil absensi: ${response.message()}"
                    }
                    Log.e("AbsensiOrangTuaVM", "Gagal mengambil absensi: ${response.code()}, Error: $errorBody")
                    absensiList.value = emptyList()
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil absensi: ${e.message}"
                Log.e("AbsensiOrangTuaVM", "Error mengambil absensi: ${e.message}", e)
                absensiList.value = emptyList()
            } finally {
                isLoading.value = false
                Log.d("AbsensiOrangTuaVM", "Fetch selesai, isLoading: ${isLoading.value}")
            }
        }
    }

    private fun updateStatistics() {
        hadirCount.value = absensiList.value.count { it.status.lowercase() == "hadir" }
        absenCount.value = absensiList.value.count { it.status.lowercase() == "alpa" }
        izinCount.value = absensiList.value.count { it.status.lowercase() == "izin" }
        sakitCount.value = absensiList.value.count { it.status.lowercase() == "sakit" }
        Log.d("AbsensiOrangTuaVM", "Statistik diperbarui: Hadir=${hadirCount.value}, Alpa=${absenCount.value}, Izin=${izinCount.value}, Sakit=${sakitCount.value}")
    }
}
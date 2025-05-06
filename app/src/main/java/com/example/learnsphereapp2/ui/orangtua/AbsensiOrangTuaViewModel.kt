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

class AbsensiOrangTuaViewModel(
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {
    val absensiList = mutableStateOf<List<AbsensiResponse>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private val formatterApi = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun fetchAbsensiByStudent(date: LocalDate) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val siswaId = preferencesHelper.getSiswaId() ?: run {
                    errorMessage.value = "ID siswa tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    Log.e("AbsensiOrangTuaViewModel", "siswaId tidak ditemukan di PreferencesHelper")
                    return@launch
                }
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    Log.e("AbsensiOrangTuaViewModel", "Token tidak ditemukan")
                    return@launch
                }
                val startDate = date.withDayOfMonth(1).format(formatterApi)
                val endDate = date.withDayOfMonth(date.lengthOfMonth()).format(formatterApi)
                Log.d("AbsensiOrangTuaViewModel", "Mengambil absensi: siswaId=$siswaId, startDate=$startDate, endDate=$endDate, token=$token")

                val response = RetrofitClient.apiService.getAbsensiByStudent(
                    authorization = "Bearer $token",
                    siswaId = siswaId,
                    startDate = startDate,
                    endDate = endDate
                )
                Log.d("AbsensiOrangTuaViewModel", "Response kode: ${response.code()}, body: ${response.body()}")
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    absensiList.value = data
                    if (data.isEmpty()) {
                        errorMessage.value = "Tidak ada data absensi untuk bulan ini. Pastikan guru telah mengisi absensi atau coba refresh."
                        Log.w("AbsensiOrangTuaViewModel", "AbsensiList kosong untuk siswaId=$siswaId")
                    } else {
                        Log.d("AbsensiOrangTuaViewModel", "Berhasil mengambil ${data.size} data absensi: ${data.map { "${it.tanggal}: ${it.status}" }}")
                    }
                } else {
                    errorMessage.value = when (response.code()) {
                        404 -> "Absensi tidak ditemukan untuk siswa ini. Pastikan ID siswa benar."
                        403 -> "Akses ditolak. Silakan login kembali atau hubungi administrator."
                        else -> "Gagal mengambil absensi: ${response.message()}"
                    }
                    Log.e("AbsensiOrangTuaViewModel", "Gagal mengambil absensi: ${response.code()} - ${response.message()}")
                    absensiList.value = emptyList()
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil absensi: ${e.message}. Coba lagi nanti."
                Log.e("AbsensiOrangTuaViewModel", "Error mengambil absensi: ${e.message}", e)
                absensiList.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }
}
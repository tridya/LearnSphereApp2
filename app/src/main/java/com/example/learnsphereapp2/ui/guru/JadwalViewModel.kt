package com.example.learnsphereapp2.ui.guru

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.JadwalCreate
import com.example.learnsphereapp2.data.model.JadwalResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch

class JadwalViewModel(
    private val preferencesHelper: PreferencesHelper,
    private val kelasId: Int
) : ViewModel() {
    val currentJadwalList = mutableStateListOf<JadwalResponse>()
    val allJadwalList = mutableStateListOf<JadwalResponse>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private val token: String?
        get() = preferencesHelper.getToken()

    init {
        fetchCurrentJadwal() // Default: ambil jadwal saat ini
    }

    fun fetchCurrentJadwal() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    return@launch
                }
                Log.d("JadwalViewModel", "Mengambil jadwal saat ini untuk kelasId: $kelasId dari endpoint /api/jadwal/kelas/$kelasId/current")
                val jadwalResponse = RetrofitClient.apiService.getCurrentJadwalByKelas(
                    authorization = "Bearer $token",
                    kelasId = kelasId
                )
                Log.d("JadwalViewModel", "Response code (current): ${jadwalResponse.code()}")
                Log.d("JadwalViewModel", "Response body (current): ${jadwalResponse.body()}")
                if (jadwalResponse.isSuccessful) {
                    currentJadwalList.clear()
                    jadwalResponse.body()?.let {
                        currentJadwalList.addAll(it)
                        Log.d("JadwalViewModel", "Berhasil mengambil ${currentJadwalList.size} jadwal saat ini: $currentJadwalList")
                    } ?: run {
                        Log.d("JadwalViewModel", "Tidak ada jadwal saat ini untuk kelas $kelasId")
                    }
                } else {
                    val errorBody = jadwalResponse.errorBody()?.string()
                    errorMessage.value = when (jadwalResponse.code()) {
                        403 -> "Anda tidak memiliki akses ke kelas ini."
                        404 -> "Jadwal tidak ditemukan untuk kelas ini."
                        else -> "Gagal mengambil jadwal saat ini: ${jadwalResponse.code()} - $errorBody"
                    }
                    Log.e("JadwalViewModel", "Error (current): ${jadwalResponse.code()} - $errorBody")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil jadwal saat ini: ${e.message}"
                Log.e("JadwalViewModel", "Exception (current): ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchAllJadwal() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    return@launch
                }
                Log.d("JadwalViewModel", "Mengambil semua jadwal untuk kelasId: $kelasId dari endpoint /api/jadwal/kelas/$kelasId")
                val jadwalResponse = RetrofitClient.apiService.getAllJadwalByKelas(
                    authorization = "Bearer $token",
                    kelasId = kelasId
                )
                Log.d("JadwalViewModel", "Response code (all): ${jadwalResponse.code()}")
                Log.d("JadwalViewModel", "Response body (all): ${jadwalResponse.body()}")
                if (jadwalResponse.isSuccessful) {
                    allJadwalList.clear()
                    jadwalResponse.body()?.let {
                        allJadwalList.addAll(it)
                        Log.d("JadwalViewModel", "Berhasil mengambil ${allJadwalList.size} jadwal mingguan: $allJadwalList")
                    } ?: run {
                        Log.d("JadwalViewModel", "Tidak ada jadwal untuk kelas $kelasId")
                    }
                } else {
                    val errorBody = jadwalResponse.errorBody()?.string()
                    errorMessage.value = when (jadwalResponse.code()) {
                        403 -> "Anda tidak memiliki akses ke kelas ini."
                        404 -> "Jadwal tidak ditemukan untuk kelas ini."
                        else -> "Gagal mengambil semua jadwal: ${jadwalResponse.code()} - $errorBody"
                    }
                    Log.e("JadwalViewModel", "Error (all): ${jadwalResponse.code()} - $errorBody")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil semua jadwal: ${e.message}"
                Log.e("JadwalViewModel", "Exception (all): ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun createJadwal(
        kelasId: Int,
        hari: String,
        jamMulai: String,
        jamSelesai: String,
        mataPelajaranId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    onError("Token tidak ditemukan. Silakan login kembali.")
                    return@launch
                }
                val jadwalCreate = JadwalCreate(
                    kelasId = kelasId,
                    hari = hari,
                    jamMulai = jamMulai,
                    jamSelesai = jamSelesai,
                    mataPelajaranId = mataPelajaranId
                )
                Log.d("JadwalViewModel", "Membuat jadwal baru: $jadwalCreate")
                val response = RetrofitClient.apiService.createJadwal(
                    authorization = "Bearer $token",
                    jadwal = jadwalCreate
                )
                Log.d("JadwalViewModel", "Response code (create): ${response.code()}")
                Log.d("JadwalViewModel", "Response body (create): ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        allJadwalList.add(it) // Tambahkan jadwal baru ke daftar
                        Log.d("JadwalViewModel", "Berhasil membuat jadwal: $it")
                        onSuccess()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = when (response.code()) {
                        403 -> "Anda tidak memiliki akses untuk membuat jadwal di kelas ini."
                        404 -> "Kelas atau mata pelajaran tidak ditemukan."
                        else -> "Gagal membuat jadwal: ${response.code()} - $errorBody"
                    }
                    errorMessage.value = errorMsg
                    Log.e("JadwalViewModel", "Error (create): ${response.code()} - $errorBody")
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat membuat jadwal: ${e.message}"
                Log.e("JadwalViewModel", "Exception (create): ${e.message}", e)
                onError("Error saat membuat jadwal: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteJadwal(
        jadwalId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    onError("Token tidak ditemukan. Silakan login kembali.")
                    return@launch
                }
                Log.d("JadwalViewModel", "Menghapus jadwal dengan jadwalId: $jadwalId")
                val response = RetrofitClient.apiService.deleteJadwal(
                    authorization = "Bearer $token",
                    jadwalId = jadwalId
                )
                Log.d("JadwalViewModel", "Response code (delete): ${response.code()}")
                Log.d("JadwalViewModel", "Response body (delete): ${response.body()}")
                if (response.isSuccessful) {
                    allJadwalList.removeAll { it.jadwalId == jadwalId } // Hapus dari daftar lokal
                    Log.d("JadwalViewModel", "Berhasil menghapus jadwal dengan jadwalId: $jadwalId")
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = when (response.code()) {
                        403 -> "Anda tidak memiliki akses untuk menghapus jadwal ini."
                        404 -> "Jadwal tidak ditemukan."
                        else -> "Gagal menghapus jadwal: ${response.code()} - $errorBody"
                    }
                    errorMessage.value = errorMsg
                    Log.e("JadwalViewModel", "Error (delete): ${response.code()} - $errorBody")
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat menghapus jadwal: ${e.message}"
                Log.e("JadwalViewModel", "Exception (delete): ${e.message}", e)
                onError("Error saat menghapus jadwal: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
}

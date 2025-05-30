package com.example.learnsphereapp2.ui.guru

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.JadwalCreate
import com.example.learnsphereapp2.data.model.JadwalResponse
import com.example.learnsphereapp2.data.model.KelasResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JadwalViewModel(
    private val preferencesHelper: PreferencesHelper,
    private var kelasId: Int
) : ViewModel() {
    val currentJadwalList = mutableStateListOf<JadwalResponse>()
    val allJadwalList = mutableStateListOf<JadwalResponse>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    private val _kelasList = MutableStateFlow<List<KelasResponse>>(emptyList())
    val kelasList: StateFlow<List<KelasResponse>> = _kelasList

    private val token: String?
        get() = preferencesHelper.getToken()

    init {
        if (kelasId != -1) {
            fetchCurrentJadwal()
        }
    }


    fun fetchKelasByTeacher() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    return@launch
                }
                val kelasResponse = RetrofitClient.apiService.getKelasByTeacher(
                    authorization = "Bearer $token"
                )
                if (kelasResponse.isSuccessful) {
                    _kelasList.value = kelasResponse.body() ?: emptyList()
                    if (kelasId == -1 && _kelasList.value.isNotEmpty()) {
                        kelasId = _kelasList.value.first().kelasId
                        fetchCurrentJadwal()
                    }
                } else {
                    errorMessage.value = when (kelasResponse.code()) {
                        403 -> "Anda tidak memiliki akses untuk melihat kelas."
                        404 -> "Tidak ada kelas yang ditemukan."
                        else -> "Gagal mengambil daftar kelas."
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil daftar kelas."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchCurrentJadwal() {
        if (kelasId == -1) return
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    return@launch
                }
                val jadwalResponse = RetrofitClient.apiService.getCurrentJadwalByKelas(
                    authorization = "Bearer $token",
                    kelasId = kelasId
                )
                if (jadwalResponse.isSuccessful) {
                    currentJadwalList.clear()
                    jadwalResponse.body()?.let {
                        currentJadwalList.addAll(it)
                    }
                } else {
                    errorMessage.value = when (jadwalResponse.code()) {
                        403 -> "Anda tidak memiliki akses ke kelas ini."
                        404 -> "Jadwal tidak ditemukan untuk kelas ini."
                        else -> "Gagal mengambil jadwal saat ini."
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil jadwal saat ini."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchAllJadwal() {
        if (kelasId == -1) return
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    return@launch
                }
                val jadwalResponse = RetrofitClient.apiService.getAllJadwalByKelas(
                    authorization = "Bearer $token",
                    kelasId = kelasId
                )
                if (jadwalResponse.isSuccessful) {
                    allJadwalList.clear()
                    jadwalResponse.body()?.let {
                        allJadwalList.addAll(it)
                    }
                } else {
                    errorMessage.value = when (jadwalResponse.code()) {
                        403 -> "Anda tidak memiliki akses ke kelas ini."
                        404 -> "Jadwal tidak ditemukan untuk kelas ini."
                        else -> "Gagal mengambil semua jadwal."
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil semua jadwal."
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
                val response = RetrofitClient.apiService.createJadwal(
                    authorization = "Bearer $token",
                    jadwal = jadwalCreate
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        allJadwalList.add(it)
                        onSuccess()
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        403 -> "Anda tidak memiliki akses untuk membuat jadwal di kelas ini."
                        404 -> "Kelas atau mata pelajaran tidak ditemukan."
                        else -> "Gagal membuat jadwal."
                    }
                    errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat membuat jadwal."
                onError("Error saat membuat jadwal.")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateJadwal(
        jadwalId: Int,
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
                val response = RetrofitClient.apiService.updateJadwal(
                    authorization = "Bearer $token",
                    jadwalId = jadwalId,
                    jadwal = jadwalCreate
                )
                if (response.isSuccessful) {
                    response.body()?.let { updatedJadwal ->
                        val index = allJadwalList.indexOfFirst { it.jadwalId == jadwalId }
                        if (index != -1) {
                            allJadwalList[index] = updatedJadwal
                        }
                        onSuccess()
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        403 -> "Anda tidak memiliki akses untuk mengubah jadwal ini."
                        404 -> "Jadwal atau mata pelajaran tidak ditemukan."
                        else -> "Gagal mengupdate jadwal."
                    }
                    errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengupdate jadwal."
                onError("Error saat mengupdate jadwal.")
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
                val response = RetrofitClient.apiService.deleteJadwal(
                    authorization = "Bearer $token",
                    jadwalId = jadwalId
                )
                if (response.isSuccessful) {
                    allJadwalList.removeAll { it.jadwalId == jadwalId }
                    onSuccess()
                } else {
                    val errorMsg = when (response.code()) {
                        403 -> "Anda tidak memiliki akses untuk menghapus jadwal ini."
                        404 -> "Jadwal tidak ditemukan."
                        else -> "Gagal menghapus jadwal."
                    }
                    errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat menghapus jadwal."
                onError("Error saat menghapus jadwal.")
            } finally {
                isLoading.value = false
            }
        }
    }
}
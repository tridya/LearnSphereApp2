package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.JadwalResponse
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class JadwalOrangTuaViewModel(
    private val preferencesHelper: PreferencesHelper,
    private val initialSiswaId: Int = 0
) : ViewModel() {
    val currentJadwalList = mutableStateListOf<JadwalResponse>()
    val allJadwalList = mutableStateListOf<JadwalResponse>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    private val _siswaList = MutableStateFlow<List<SiswaResponse>>(emptyList())
    val siswaList: StateFlow<List<SiswaResponse>> = _siswaList

    private val token: String?
        get() = preferencesHelper.getToken()

    init {
        if (initialSiswaId > 0) {
            fetchCurrentJadwal(initialSiswaId)
            fetchAllJadwal(initialSiswaId)
        } else {
            Log.w("JadwalOrangTuaViewModel", "Invalid initialSiswaId: $initialSiswaId")
        }
    }

    fun fetchSiswaByParent() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    Log.e("JadwalOrangTuaViewModel", "No token found")
                    return@launch
                }
                val response = RetrofitClient.apiService.getSiswaByParent(
                    authorization = "Bearer $token"
                )
                if (response.isSuccessful) {
                    _siswaList.value = response.body() ?: emptyList()
                    Log.d("JadwalOrangTuaViewModel", "Siswa fetched: ${_siswaList.value}")
                } else {
                    errorMessage.value = "Gagal mengambil daftar siswa: ${response.code()} - ${response.message()}"
                    Log.e("JadwalOrangTuaViewModel", "Failed to fetch siswa: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil daftar siswa: ${e.message}"
                Log.e("JadwalOrangTuaViewModel", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchCurrentJadwal(siswaId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                if (siswaId <= 0) {
                    errorMessage.value = "SiswaId tidak valid: $siswaId"
                    Log.e("JadwalOrangTuaViewModel", "Invalid siswaId: $siswaId")
                    return@launch
                }
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    Log.e("JadwalOrangTuaViewModel", "No token found")
                    return@launch
                }
                Log.d("JadwalOrangTuaViewModel", "Fetching current jadwal for siswaId: $siswaId")
                val jadwalResponse = RetrofitClient.apiService.getCurrentJadwalBySiswa(
                    authorization = "Bearer $token",
                    siswaId = siswaId
                )
                if (jadwalResponse.isSuccessful) {
                    currentJadwalList.clear()
                    jadwalResponse.body()?.let {
                        currentJadwalList.addAll(it)
                    }
                    Log.d("JadwalOrangTuaViewModel", "Current jadwal fetched: $currentJadwalList")
                } else {
                    errorMessage.value = "Gagal mengambil jadwal saat ini: ${jadwalResponse.code()} - ${jadwalResponse.message()}"
                    Log.e("JadwalOrangTuaViewModel", "Failed to fetch current jadwal: ${jadwalResponse.code()} - ${jadwalResponse.message()}")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil jadwal saat ini: ${e.message}"
                Log.e("JadwalOrangTuaViewModel", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchAllJadwal(siswaId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                if (siswaId <= 0) {
                    errorMessage.value = "SiswaId tidak valid: $siswaId"
                    Log.e("JadwalOrangTuaViewModel", "Invalid siswaId: $siswaId")
                    return@launch
                }
                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    Log.e("JadwalOrangTuaViewModel", "No token found")
                    return@launch
                }
                Log.d("JadwalOrangTuaViewModel", "Fetching all jadwal for siswaId: $siswaId")
                val jadwalResponse = RetrofitClient.apiService.getJadwalBySiswa(
                    authorization = "Bearer $token",
                    siswaId = siswaId
                )
                if (jadwalResponse.isSuccessful) {
                    allJadwalList.clear()
                    jadwalResponse.body()?.let {
                        allJadwalList.addAll(it)
                    }
                    Log.d("JadwalOrangTuaViewModel", "All jadwal fetched: $allJadwalList")
                } else {
                    errorMessage.value = "Gagal mengambil semua jadwal: ${jadwalResponse.code()} - ${jadwalResponse.message()}"
                    Log.e("JadwalOrangTuaViewModel", "Failed to fetch all jadwal: ${jadwalResponse.code()} - ${jadwalResponse.message()}")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error saat mengambil semua jadwal: ${e.message}"
                Log.e("JadwalOrangTuaViewModel", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}
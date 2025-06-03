package com.example.learnsphereapp2.ui.orangtua

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.MataPelajaranResponse
import com.example.learnsphereapp2.data.model.RekapanSiswaResponse
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.network.ApiService
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrangTuaViewModel(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    // State for students
    private val _students = MutableStateFlow<List<SiswaResponse>>(emptyList())
    val students: StateFlow<List<SiswaResponse>> = _students

    // State for subjects
    private val _subjects = MutableStateFlow<List<MataPelajaranResponse>>(emptyList())
    val subjects: StateFlow<List<MataPelajaranResponse>> = _subjects

    // State for reports
    private val _reports = MutableStateFlow<List<RekapanSiswaResponse>>(emptyList())
    val reports: StateFlow<List<RekapanSiswaResponse>> = _reports

    // State for loading and error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Selected student ID
    private val _selectedSiswaId = MutableStateFlow<Int?>(null)
    val selectedSiswaId: StateFlow<Int?> = _selectedSiswaId

    init {
        fetchStudents()
    }

    fun setSelectedSiswaId(siswaId: Int) {
        _selectedSiswaId.value = siswaId
        fetchSubjects()
    }

    private fun fetchStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = preferencesHelper.getToken() ?: throw Exception("No token found")
                val response = apiService.getSiswaOrangTua("Bearer $token")
                _students.value = response
                if (response.isNotEmpty()) {
                    _selectedSiswaId.value = response.first().siswaId
                    fetchSubjects()
                }
            } catch (e: Exception) {
                _error.value = "Gagal memuat data siswa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchSubjects() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = preferencesHelper.getToken() ?: throw Exception("No token found")
                val response = apiService.getMataPelajaran("Bearer $token")
                _subjects.value = response
            } catch (e: Exception) {
                _error.value = "Gagal memuat daftar mata pelajaran: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchReports(siswaId: Int, mataPelajaranId: Int, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = preferencesHelper.getToken() ?: throw Exception("No token found")
                val response = apiService.getRekapanSiswaOrangTua(
                    token = "Bearer $token",
                    siswaId = siswaId,
                    startDate = startDate ?: "2025-01-01",
                    endDate = endDate ?: "2025-12-31",
                    mataPelajaranId = mataPelajaranId
                )
                _reports.value = response
            } catch (e: Exception) {
                _error.value = "Gagal memuat rekapan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class OrangTuaViewModelFactory(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrangTuaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrangTuaViewModel(apiService, preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
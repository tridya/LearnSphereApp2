//package com.example.learnsphereapp2.ui.orangtua
//
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.learnsphereapp2.data.model.SiswaResponse
//import com.example.learnsphereapp2.network.RetrofitClient
//import com.example.learnsphereapp2.util.PreferencesHelper
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class OrangTuaViewModel(
//    private val preferencesHelper: PreferencesHelper
//) : ViewModel() {
//    private val _student = MutableStateFlow<SiswaResponse?>(null)
//    val student: StateFlow<SiswaResponse?> = _student
//    val isLoading = mutableStateOf(false)
//    val errorMessage = mutableStateOf<String?>(null)
//
//    init {
//        fetchStudentByParent()
//    }
//
//    private fun fetchStudentByParent() {
//        viewModelScope.launch {
//            isLoading.value = true
//            errorMessage.value = null
//            try {
//                val token = preferencesHelper.getToken() ?: run {
//                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
//                    isLoading.value = false
//                    return@launch
//                }
//                val parentId = preferencesHelper.getUserId() ?: run {
//                    errorMessage.value = "ID orang tua tidak ditemukan."
//                    isLoading.value = false
//                    return@launch
//                }
//                val response = RetrofitClient.apiService.getStudentsByParent(
//                    authorization = "Bearer $token",
//                    parentId = parentId
//                )
//                if (response.isSuccessful) {
//                    val students = response.body() ?: emptyList()
//                    if (students.isNotEmpty()) {
//                        _student.value = students.first() // Assuming one child for simplicity
//                    } else {
//                        errorMessage.value = "Tidak ada data siswa ditemukan untuk orang tua ini."
//                    }
//                } else {
//                    errorMessage.value = when (response.code()) {
//                        403 -> "Anda tidak memiliki akses."
//                        404 -> "Data siswa tidak ditemukan."
//                        else -> "Gagal mengambil data siswa: ${response.message()}"
//                    }
//                }
//            } catch (e: Exception) {
//                errorMessage.value = "Error saat mengambil data siswa: ${e.message}"
//            } finally {
//                isLoading.value = false
//            }
//        }
//    }
//}
//
//class OrangTuaViewModelFactory(
//    private val preferencesHelper: PreferencesHelper
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(OrangTuaViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return OrangTuaViewModel(preferencesHelper) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
package com.example.learnsphereapp2.ui.guru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.*
import com.example.learnsphereapp2.network.ApiService
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class RekapanViewModel(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {
    private val _rekapanList = MutableStateFlow<List<StatusRekapanSiswa>>(emptyList())
    val rekapanList: StateFlow<List<StatusRekapanSiswa>> = _rekapanList.asStateFlow()

    private val _jadwalList = MutableStateFlow<List<JadwalResponse>>(emptyList())
    val jadwalList: StateFlow<List<JadwalResponse>> = _jadwalList.asStateFlow()

    private val _mataPelajaran = MutableStateFlow<List<MataPelajaranResponse>>(emptyList())
    val mataPelajaran: StateFlow<List<MataPelajaranResponse>> = _mataPelajaran.asStateFlow()

    private val _kelas = MutableStateFlow<List<KelasResponse>>(emptyList())
    val kelas: StateFlow<List<KelasResponse>> = _kelas.asStateFlow()

    private val _siswaList = MutableStateFlow<List<SiswaResponse>>(emptyList())
    val siswaList: StateFlow<List<SiswaResponse>> = _siswaList.asStateFlow()

    private val _dailyRekapanList = MutableStateFlow<List<RekapanSiswaResponse>>(emptyList())
    val dailyRekapanList: StateFlow<List<RekapanSiswaResponse>> = _dailyRekapanList.asStateFlow()

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    private var guruId: Int? = preferencesHelper.getUserId()

    init {
        // Validasi guru_id saat inisialisasi
        if (guruId == null || guruId!! <= 0) {
            _errorMessage.value = "ID guru tidak valid. Silakan login kembali."
            _navigateToLogin.value = true
        }
    }

    private suspend fun <T> withValidToken(
        token: String,
        block: suspend (String) -> T
    ): T? {
        if (token.isBlank()) {
            _errorMessage.value = "Token autentikasi tidak ditemukan. Silakan login kembali."
            _navigateToLogin.value = true
            Log.e("RekapanViewModel", "Token is empty")
            return null
        }
        return try {
            block("Bearer $token")
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "No error body"
            val errorMsg = when (e.code()) {
                401 -> {
                    _navigateToLogin.value = true
                    "Sesi Anda telah berakhir. Silakan login kembali."
                }
                403 -> "Akses ditolak."
                else -> "Gagal: ${e.message()} (HTTP ${e.code()}) Detail: $errorBody"
            }
            Log.e("RekapanViewModel", errorMsg, e)
            _errorMessage.value = errorMsg
            null
        } catch (e: ConnectException) {
            val errorMsg = "Tidak dapat terhubung ke server. Periksa koneksi jaringan Anda."
            Log.e("RekapanViewModel", errorMsg, e)
            _errorMessage.value = errorMsg
            null
        } catch (e: SocketTimeoutException) {
            val errorMsg = "Koneksi ke server terputus (timeout). Coba lagi nanti."
            Log.e("RekapanViewModel", errorMsg, e)
            _errorMessage.value = errorMsg
            null
        } catch (e: IOException) {
            val errorMsg = "Masalah jaringan. Periksa koneksi internet."
            Log.e("RekapanViewModel", errorMsg, e)
            _errorMessage.value = errorMsg
            null
        } catch (e: Exception) {
            val errorMsg = "Terjadi kesalahan: ${e.message ?: "Tidak diketahui"}"
            Log.e("RekapanViewModel", errorMsg, e)
            _errorMessage.value = errorMsg
            null
        }
    }

    fun fetchCurrentUser(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                Log.d("RekapanViewModel", "Fetching current user with token: $authToken")
                val user = apiService.getUser(authToken).body()
                if (user != null) {
                    _currentUser.value = user
                    guruId = user.id
                    preferencesHelper.saveUserData(
                        userId = user.id,
                        username = user.username,
                        nama = user.nama,
                        role = user.role,
                        kelasId = null
                    )
                    Log.d("RekapanViewModel", "User fetched: $user, guruId set to ${user.id}")
                } else {
                    _errorMessage.value = "Gagal mengambil data pengguna: Respons kosong"
                    Log.e("RekapanViewModel", "Empty user response")
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchRekapanByKelas(kelasId: Int, mataPelajaranId: Int, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                Log.d("RekapanViewModel", "Fetching rekapan for kelasId: $kelasId, mataPelajaranId: $mataPelajaranId")
                val rekapan = apiService.getRekapanByKelas(
                    kelasId = kelasId,
                    mataPelajaranId = mataPelajaranId,
                    token = authToken
                )
                Log.d("RekapanViewModel", "Rekapan fetched: ${rekapan.size} items, data: $rekapan")
                _rekapanList.value = rekapan
                if (rekapan.isEmpty()) {
                    Log.w("RekapanViewModel", "No rekapan found for kelasId: $kelasId, mataPelajaranId: $mataPelajaranId")
                    // Tidak dianggap error, hanya status informatif
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchStudentsByClass(kelasId: Int, token: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && _siswaList.value.isNotEmpty()) {
            Log.d("RekapanViewModel", "Students already fetched, skip fetching")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                Log.d("RekapanViewModel", "Fetching students for kelasId: $kelasId")
                val response = apiService.getStudentsByClass(authorization = authToken, kelasId = kelasId)
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    Log.d("RekapanViewModel", "Students fetched: ${students.size} items, data: $students")
                    _siswaList.value = students
                    if (students.isEmpty()) {
                        Log.w("RekapanViewModel", "No students found for kelasId: $kelasId")
                        // Tidak dianggap error, hanya status informatif
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        404 -> "Data siswa tidak ditemukan untuk kelas ini."
                        else -> "Gagal mengambil siswa: ${response.message()} (HTTP ${response.code()})"
                    }
                    Log.e("RekapanViewModel", errorMsg)
                    _errorMessage.value = errorMsg
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchJadwalByKelas(kelasId: Int, token: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && _jadwalList.value.isNotEmpty()) {
            Log.d("RekapanViewModel", "Jadwal already fetched, skip fetching")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                Log.d("RekapanViewModel", "Fetching jadwal for kelasId: $kelasId")
                val jadwal = apiService.getJadwalByKelas(
                    kelasId = kelasId,
                    token = authToken
                )
                Log.d("RekapanViewModel", "Jadwal fetched: ${jadwal.size} items")
                _jadwalList.value = jadwal
                if (jadwal.isEmpty() && _mataPelajaran.value.isNotEmpty()) {
                    Log.d("RekapanViewModel", "Jadwal kosong, menggunakan mata pelajaran pertama")
                    val defaultMataPelajaranId = _mataPelajaran.value.first().mataPelajaranId
                    fetchRekapanByKelas(kelasId, defaultMataPelajaranId, token)
                } else if (jadwal.isNotEmpty()) {
                    val defaultMataPelajaranId = jadwal.first().mataPelajaranId
                    fetchRekapanByKelas(kelasId, defaultMataPelajaranId, token)
                }
            }
            _isLoading.value = false
        }
    }

    fun createDailyRekapan(rekapan: RekapanSiswaCreate, token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                if (guruId == null || guruId!! <= 0) {
                    _errorMessage.value = "ID guru tidak valid. Silakan login kembali."
                    _navigateToLogin.value = true
                    Log.e("RekapanViewModel", "Invalid guru_id: $guruId")
                    return@withValidToken
                }

                val validRatings = listOf("Sangat Baik", "Baik", "Cukup", "Kurang", "Buruk")
                if (rekapan.rating !in validRatings) {
                    _errorMessage.value = "Rating tidak valid. Pilih: ${validRatings.joinToString()}"
                    Log.e("RekapanViewModel", "Invalid rating: ${rekapan.rating}")
                    return@withValidToken
                }

                if (rekapan.siswa_id <= 0) {
                    _errorMessage.value = "ID siswa tidak valid. Pilih siswa yang valid."
                    Log.e("RekapanViewModel", "Invalid siswa_id: ${rekapan.siswa_id}")
                    return@withValidToken
                }

                if (rekapan.mata_pelajaran_id <= 0) {
                    _errorMessage.value = "ID mata pelajaran tidak valid. Pilih mata pelajaran yang valid."
                    Log.e("RekapanViewModel", "Invalid mata_pelajaran_id: ${rekapan.mata_pelajaran_id}")
                    return@withValidToken
                }

                if (rekapan.guru_id != guruId) {
                    _errorMessage.value = "ID guru tidak sesuai dengan pengguna saat ini."
                    Log.e("RekapanViewModel", "guru_id mismatch: ${rekapan.guru_id} != $guruId")
                    return@withValidToken
                }

                Log.d("RekapanViewModel", "Creating rekapan: $rekapan")
                val response = apiService.createDailyRekapan(
                    rekapan = rekapan,
                    token = authToken
                )
                Log.d("RekapanViewModel", "Rekapan created successfully: $response")
                _errorMessage.value = "Rekapan berhasil disimpan"
                onSuccess()
            }
            _isLoading.value = false
        }
    }

    fun fetchMataPelajaran(token: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && _mataPelajaran.value.isNotEmpty()) {
            Log.d("RekapanViewModel", "Mata pelajaran sudah ada, skip fetching")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                Log.d("RekapanViewModel", "Fetching mata pelajaran")
                val mataPelajaran = apiService.getMataPelajaran(token = authToken)
                Log.d("RekapanViewModel", "Mata pelajaran fetched: ${mataPelajaran.size} items")
                _mataPelajaran.value = mataPelajaran
                if (mataPelajaran.isEmpty()) {
                    Log.w("RekapanViewModel", "No mata pelajaran found")
                    // Tidak dianggap error, hanya status informatif
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchKelas(token: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && _kelas.value.isNotEmpty()) {
            Log.d("RekapanViewModel", "Kelas sudah ada, skip fetching")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                Log.d("RekapanViewModel", "Fetching kelas")
                val kelasList = apiService.getKelas(token = authToken)
                Log.d("RekapanViewModel", "Kelas fetched: ${kelasList.size} items, data: $kelasList")
                _kelas.value = kelasList
                if (kelasList.isEmpty()) {
                    Log.w("RekapanViewModel", "No kelas found")
                    // Tidak dianggap error, hanya status informatif
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchDailyRekapan(kelasId: Int, tanggal: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            withValidToken(token) { authToken ->
                Log.d("RekapanViewModel", "Fetching daily rekapan for kelasId: $kelasId, tanggal: $tanggal")
                val rekapan = apiService.getDailyRekapan(
                    kelasId = kelasId,
                    tanggal = tanggal,
                    token = authToken
                )
                Log.d("RekapanViewModel", "Daily rekapan fetched: ${rekapan.size} items")
                _dailyRekapanList.value = rekapan
                if (rekapan.isEmpty()) {
                    Log.w("RekapanViewModel", "No rekapan found for tanggal: $tanggal")
                    // Tidak dianggap error, hanya status informatif
                }
            }
            _isLoading.value = false
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun resetNavigation() {
        _navigateToLogin.value = false
    }
}
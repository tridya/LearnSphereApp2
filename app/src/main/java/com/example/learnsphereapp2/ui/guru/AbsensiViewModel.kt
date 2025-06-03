package com.example.learnsphereapp2.ui.guru

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.AbsensiCreate
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.Holiday
import com.example.learnsphereapp2.data.model.SemesterHoliday
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek
import java.text.SimpleDateFormat
import java.util.*

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
    val isAbsensiEnabled = mutableStateOf(true)

    private var nationalHolidays: List<Holiday> = emptyList()

    // Data libur semester didefinisikan secara lokal
    private val semesterHolidays: List<SemesterHoliday> = listOf(
        SemesterHoliday(
            startDate = "2024-12-23",
            endDate = "2025-01-04",
            description = "Libur Semester Ganjil 2024/2025"
        ),
        SemesterHoliday(
            startDate = "2025-06-23",
            endDate = "2025-07-12",
            description = "Libur Semester Genap 2024/2025"
        ),
        SemesterHoliday(
            startDate = "2025-12-22",
            endDate = "2026-01-02",
            description = "Libur Semester Ganjil 2025/2026"
        )
        // Tambahkan libur semester lainnya jika ada
    )

    private val formatterApi = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        // Ambil data hari libur nasional saat ViewModel diinisialisasi
        fetchNationalHolidays()
    }

    private fun fetchNationalHolidays(year: Int = 2025) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getNationalHolidays(year)
                if (response.isSuccessful) {
                    nationalHolidays = response.body() ?: emptyList()
                    Log.d("AbsensiViewModel", "Berhasil mengambil ${nationalHolidays.size} hari libur nasional")
                } else {
                    Log.e("AbsensiViewModel", "Gagal mengambil hari libur nasional: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AbsensiViewModel", "Error mengambil hari libur nasional: ${e.message}", e)
            }
        }
    }

    fun fetchData(kelasId: Int, tanggal: LocalDate?) {
        if (tanggal == null) {
            errorMessage.value = "Tanggal tidak valid."
            isAbsensiEnabled.value = false
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                // Validasi tanggal
                val tanggalApi = tanggal.format(formatterApi)
                isAbsensiEnabled.value = validateDate(tanggalApi)
                if (!isAbsensiEnabled.value) {
                    return@launch
                }

                val token = preferencesHelper.getToken() ?: run {
                    errorMessage.value = "Token tidak ditemukan. Silakan login kembali."
                    isLoading.value = false
                    isAbsensiEnabled.value = false
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
        if (!isAbsensiEnabled.value) {
            return
        }
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

    private fun isSunday(date: LocalDate): Boolean {
        return date.dayOfWeek == DayOfWeek.SUNDAY
    }

    private fun validateDate(tanggal: String): Boolean {
        try {
            // Parse tanggal ke Date object
            val date: Date = dateFormat.parse(tanggal) ?: run {
                errorMessage.value = "Format tanggal salah. Gunakan YYYY-MM-DD"
                return false
            }

            // Cek hari Minggu
            val calendar = Calendar.getInstance().apply { time = date }
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                errorMessage.value = "Absensi tidak diizinkan pada hari Minggu"
                return false
            }

            // Cek hari libur nasional
            if (nationalHolidays.any { it.date == tanggal }) {
                errorMessage.value = "Absensi tidak diizinkan pada hari libur nasional"
                return false
            }

            // Cek libur semester
            val tanggalDate = dateFormat.parse(tanggal)
            val isSemesterHoliday = semesterHolidays.any { holiday ->
                val startDate = dateFormat.parse(holiday.startDate)
                val endDate = dateFormat.parse(holiday.endDate)
                tanggalDate in startDate..endDate
            }
            if (isSemesterHoliday) {
                errorMessage.value = "Absensi tidak diizinkan pada masa libur semester"
                return false
            }

            return true
        } catch (e: Exception) {
            errorMessage.value = "Error memvalidasi tanggal: ${e.message}"
            return false
        }
    }
}
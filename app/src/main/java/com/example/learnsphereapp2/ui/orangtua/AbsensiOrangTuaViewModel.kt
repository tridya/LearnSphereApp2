package com.example.learnsphereapp2.ui.orangtua

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.Holiday
import com.example.learnsphereapp2.data.model.SemesterHoliday
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class AbsensiWrapper(
    val data: List<AbsensiResponse>
)

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
    val nationalHolidays = mutableStateOf<List<Holiday>>(emptyList())
    val isHolidaysLoading = mutableStateOf(true) // Tambahkan state untuk melacak status pengambilan data libur

    private val formatterApi = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Data libur semester (dari percakapan sebelumnya)
    private val semesterHolidays: List<SemesterHoliday> = listOf(
        SemesterHoliday(
            startDate = "2025-06-23",
            endDate = "2025-07-12",
            description = "Libur Semester Genap 2025"
        ),
        SemesterHoliday(
            startDate = "2025-12-20",
            endDate = "2026-01-04",
            description = "Libur Semester Ganjil 2025/2026"
        )
    )

    // Fallback data libur nasional jika API gagal
    private val fallbackNationalHolidays: List<Holiday> = listOf(
        Holiday(
            date = "2025-01-01",
            event = "Tahun Baru Masehi",
            isNationalHoliday = true
        ),
        Holiday(
            date = "2025-05-01",
            event = "Hari Buruh Internasional",
            isNationalHoliday = true
        ),
        Holiday(
            date = "2025-08-17",
            event = "Hari Kemerdekaan Indonesia",
            isNationalHoliday = true
        ),
        Holiday(
            date = "2025-12-25",
            event = "Hari Natal",
            isNationalHoliday = true
        )
    )

    init {
        // Ambil data libur nasional saat ViewModel diinisialisasi
        fetchNationalHolidays(2025) // Panggil dengan tahun saat ini atau sesuai kebutuhan
    }

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
                Log.d("AbsensiOrangTuaVM", "Mengirim header: Bearer $token")

                val startDate = date.withDayOfMonth(1).format(formatterApi)
                val endDate = date.withDayOfMonth(date.lengthOfMonth()).format(formatterApi)
                Log.d("AbsensiOrangTuaVM", "Parameter: siswa_id=$siswaId, start_date=$startDate, end_date=$endDate")

                // Jika tahun berbeda, ambil data libur nasional untuk tahun tersebut
                if (date.year != 2025) {
                    fetchNationalHolidays(date.year)
                }

                delay(500) // Cegah race condition
                val response = RetrofitClient.apiService.getAbsensiByStudent(
                    authorization = "Bearer $token",
                    siswaId = siswaId,
                    startDate = startDate,
                    endDate = endDate
                )
                Log.d("AbsensiOrangTuaVM", "Respon kode: ${response.code()}")
                val rawBody = response.errorBody()?.string() ?: response.body()?.toString() ?: "null"
                Log.d("AbsensiOrangTuaVM", "Body mentah: $rawBody")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        Log.w("AbsensiOrangTuaVM", "Body null meskipun respons 200")
                        errorMessage.value = "Gagal membaca data absensi dari server."
                        absensiList.value = emptyList()
                    } else {
                        val absensiData = if (rawBody.contains("\"data\"")) {
                            val wrapper = Gson().fromJson(rawBody, AbsensiWrapper::class.java)
                            wrapper.data ?: emptyList()
                        } else {
                            body as? List<AbsensiResponse> ?: emptyList()
                        }
                        absensiList.value = absensiData
                        if (absensiList.value.isEmpty()) {
                            Log.w("AbsensiOrangTuaVM", "Data absensi kosong meskipun respons 200")
                            errorMessage.value = "Tidak ada data absensi untuk periode ini."
                        } else {
                            updateStatistics()
                            Log.d("AbsensiOrangTuaVM", "Berhasil mengambil ${absensiList.value.size} data absensi: ${absensiList.value.map { "${it.tanggal}: ${it.status}" }}")
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Tidak ada detail error"
                    errorMessage.value = when (response.code()) {
                        401 -> "Sesi login tidak valid. Silakan login kembali."
                        403 -> "Anda tidak memiliki akses."
                        404 -> "Tidak ada data absensi untuk periode ini."
                        500 -> "Server error. Silakan coba lagi nanti."
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

    fun fetchNationalHolidays(year: Int) {
        viewModelScope.launch {
            try {
                Log.d("AbsensiOrangTuaVM", "Mengambil libur nasional untuk tahun: $year")
                val response = RetrofitClient.apiService.getNationalHolidays(year)
                Log.d("AbsensiOrangTuaVM", "Respon kode libur nasional: ${response.code()}")
                val rawBody = response.errorBody()?.string() ?: response.body()?.toString() ?: "null"
                Log.d("AbsensiOrangTuaVM", "Body mentah libur nasional: $rawBody")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        Log.w("AbsensiOrangTuaVM", "Body libur nasional null meskipun respons 200")
                        nationalHolidays.value = fallbackNationalHolidays
                        Log.d("AbsensiOrangTuaVM", "Menggunakan data fallback: ${nationalHolidays.value.size} libur nasional")
                    } else {
                        nationalHolidays.value = body.filter { it.isNationalHoliday }
                        Log.d("AbsensiOrangTuaVM", "Berhasil mengambil ${nationalHolidays.value.size} libur nasional: ${nationalHolidays.value.map { "${it.date}: ${it.event}" }}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Tidak ada detail error"
                    Log.e("AbsensiOrangTuaVM", "Gagal mengambil libur nasional: ${response.code()}, Error: $errorBody")
                    nationalHolidays.value = fallbackNationalHolidays
                    Log.d("AbsensiOrangTuaVM", "Menggunakan data fallback karena API gagal: ${nationalHolidays.value.size} libur nasional")
                }
            } catch (e: Exception) {
                Log.e("AbsensiOrangTuaVM", "Error mengambil libur nasional: ${e.message}", e)
                nationalHolidays.value = fallbackNationalHolidays
                Log.d("AbsensiOrangTuaVM", "Menggunakan data fallback karena exception: ${nationalHolidays.value.size} libur nasional")
            } finally {
                isHolidaysLoading.value = false
                Log.d("AbsensiOrangTuaVM", "Pengambilan libur nasional selesai, isHolidaysLoading: ${isHolidaysLoading.value}")
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

    fun isNationalHoliday(date: LocalDate): Boolean {
        val dateString = date.format(formatterApi)
        val isHoliday = nationalHolidays.value.any { it.date == dateString && it.isNationalHoliday }
        Log.d("AbsensiOrangTuaVM", "Cek libur nasional untuk $dateString: $isHoliday")
        return isHoliday
    }

    fun isSemesterHoliday(date: LocalDate): Boolean {
        val dateString = date.format(formatterApi)
        val isHoliday = semesterHolidays.any { holiday ->
            val startDate = LocalDate.parse(holiday.startDate, formatterApi)
            val endDate = LocalDate.parse(holiday.endDate, formatterApi)
            date in startDate..endDate
        }
        Log.d("AbsensiOrangTuaVM", "Cek libur semester untuk $dateString: $isHoliday")
        return isHoliday
    }
}
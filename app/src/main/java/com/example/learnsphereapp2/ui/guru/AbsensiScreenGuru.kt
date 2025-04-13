package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AbsensiScreenGuru(
    navController: NavController,
    kelasId: Int,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: AbsensiViewModel = viewModel(factory = AbsensiViewModelFactory<Any>(preferencesHelper))
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale("id", "ID"))
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(selectedDate.value) {
        viewModel.fetchData(kelasId = kelasId, tanggal = selectedDate.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Sama dengan AbsensiDetailScreenGuru
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Absen Harian Siswa",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn untuk scroll semua konten
        LazyColumn(
            modifier = Modifier.weight(1f) // Mengambil ruang tersedia, seperti LazyColumn di AbsensiDetailScreenGuru
        ) {
            // Kontrol bulan
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Bulan Sebelumnya",
                        modifier = Modifier
                            .clickable { currentMonth = currentMonth.minusMonths(1) }
                    )
                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale("id", "ID"))),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Bulan Berikutnya",
                        modifier = Modifier
                            .clickable { currentMonth = currentMonth.plusMonths(1) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Kalender
            item {
                CalendarView(
                    yearMonth = currentMonth,
                    onDateSelected = { date ->
                        selectedDate.value = date
                        navController.navigate("absensi_detail_guru/$kelasId/${date.format(formatter)}")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Konten utama (loading, error, atau data)
            when {
                viewModel.isLoading.value -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                viewModel.errorMessage.value != null -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.errorMessage.value ?: "Terjadi kesalahan",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    if (viewModel.siswaList.value.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatCard("Siswa Hadir", viewModel.hadirCount.value, Color(0xFF4CAF50), "Hadir")
                                StatCard("Siswa Absen", viewModel.absenCount.value, Color(0xFFF44336), "Alpa")
                                StatCard("Siswa Izin", viewModel.izinCount.value, Color(0xFF2196F3), "Izin")
                                StatCard("Siswa Sakit", viewModel.sakitCount.value, Color(0xFFFF9800), "Sakit")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Daftar Siswa",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        val sortedSiswaList = viewModel.siswaList.value.sortedBy { it.nama }
                        items(sortedSiswaList.size) { index ->
                            val siswa = sortedSiswaList[index]
                            val absensi = viewModel.absensiList.value.find { it.siswaId == siswa.siswaId }
                            SiswaItem(siswa.nama, absensi?.status ?: "Belum Diisi")
                        }
                    }
                }
                else -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatCard("Siswa Hadir", viewModel.hadirCount.value, Color(0xFF4CAF50), "Hadir")
                            StatCard("Siswa Absen", viewModel.absenCount.value, Color(0xFFF44336), "Alpa")
                            StatCard("Siswa Izin", viewModel.izinCount.value, Color(0xFF2196F3), "Izin")
                            StatCard("Siswa Sakit", viewModel.sakitCount.value, Color(0xFFFF9800), "Sakit")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Daftar Siswa",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (viewModel.siswaList.value.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tidak ada siswa ditemukan",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else {
                        val sortedSiswaList = viewModel.siswaList.value.sortedBy { it.nama }
                        items(sortedSiswaList.size) { index ->
                            val siswa = sortedSiswaList[index]
                            val absensi = viewModel.absensiList.value.find { it.siswaId == siswa.siswaId }
                            SiswaItem(siswa.nama, absensi?.status ?: "Belum Diisi")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BottomNavigationGuru(navController) // Sama persis dengan AbsensiDetailScreenGuru
    }
}

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7
    val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        var day = 1
        val totalSlots = (daysInMonth + firstDayOfMonth - 1) / 7 + 1
        for (week in 0 until totalSlots) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (i in 0 until 7) {
                    if (week == 0 && i < firstDayOfMonth || day > daysInMonth) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .weight(1f)
                        )
                    } else {
                        val date = yearMonth.atDay(day)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .weight(1f)
                                .background(
                                    if (date == LocalDate.now()) Color(0xFF6200EE) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (date == LocalDate.now()) Color.White else Color.Black
                            )
                        }
                        day++
                    }
                }
            }
        }
    }
}

@Composable
fun SiswaItem(nama: String, status: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = nama,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = status,
                color = when (status) {
                    "Hadir" -> Color(0xFF4CAF50)
                    "Alpa" -> Color(0xFFF44336)
                    "Izin" -> Color(0xFF2196F3)
                    "Sakit" -> Color(0xFFFF9800)
                    else -> Color.Gray
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

class AbsensiViewModelFactory<ViewModel>(private val preferencesHelper: PreferencesHelper) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AbsensiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AbsensiViewModel(preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
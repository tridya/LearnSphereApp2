package com.example.learnsphereapp2.ui.orangtua

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun AbsensiScreenOrangTua(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper,
    viewModel: AbsensiOrangTuaViewModel = AbsensiOrangTuaViewModel(preferencesHelper)
) {
    val absensiList by viewModel.absensiList
    val hadirCount by viewModel.hadirCount
    val absenCount by viewModel.absenCount
    val izinCount by viewModel.izinCount
    val sakitCount by viewModel.sakitCount
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(today) }
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
    val siswaIdHardcoded = 1 // Hardcode siswaId di sini
    val currentSiswaId = siswaIdHardcoded

    Log.d("AbsensiScreenOrangTua", "siswaId yang digunakan (hardcoded): $siswaIdHardcoded")

    if (currentSiswaId == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ID siswa tidak ditemukan. Silakan login kembali.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LaunchedEffect(currentMonth, currentSiswaId) {
        viewModel.fetchAbsensiByStudent(currentMonth.atDay(1), currentSiswaId)
    }

    LaunchedEffect(absensiList, selectedDate) {
        val dateKey = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val foundStatus = absensiList.find { it.tanggal == dateKey }?.status
        Log.d("AbsensiScreenOrangTua", "Tanggal dipilih: $dateKey, Status: $foundStatus")
        Log.d("AbsensiScreenOrangTua", "AbsensiList: ${absensiList.map { "${it.tanggal}: ${it.status}" }}")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            // Header dengan tombol back dan judul
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = "Absensi Harian",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

        item {
            // Tombol refresh
            Button(
                onClick = { viewModel.fetchAbsensiByStudent(currentMonth.atDay(1), currentSiswaId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FFD))
            ) {
                Text(
                    text = "Refresh Data",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            // Pesan error
            errorMessage?.let { pesan ->
                Text(
                    text = pesan,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )
            }
        }

        if (isLoading) {
            item {
                LoadingIndicator()
            }
        } else {
            item {
                // Kontrol bulan dan kalender
                MonthSelector(
                    currentMonth = currentMonth,
                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
                )
                Spacer(modifier = Modifier.height(8.dp))

                CalendarView(
                    yearMonth = currentMonth,
                    selectedDate = selectedDate,
                    absensiList = absensiList, // Teruskan absensiList ke CalendarView
                    onDateSelected = { date -> selectedDate = date }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Statistik absensi
                AttendanceStats(
                    hadir = hadirCount,
                    absen = absenCount,
                    izin = izinCount,
                    sakit = sakitCount,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                SectionTitle("Daftar Absensi")
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (absensiList.isEmpty()) {
                item {
                    EmptyState("Tidak ada data absensi untuk bulan ini")
                }
            } else {
                items(absensiList.sortedBy { it.tanggal }) { absensi ->
                    AttendanceItem(
                        tanggal = absensi.tanggal,
                        status = absensi.status,
                        onClick = {
                            Log.d("AbsensiScreenOrangTua", "Klik pada ${absensi.tanggal}: ${absensi.status}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Bulan Sebelumnya")
        }
        Text(
            text = currentMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
            ),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Bulan Berikutnya")
        }
    }
}

@Composable
private fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    absensiList: List<AbsensiResponse>, // Tambahkan parameter absensiList
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = if (yearMonth.atDay(1).dayOfWeek.value == 7) 0 else yearMonth.atDay(1).dayOfWeek.value // Pastikan Minggu = 0
    val daysOfWeek = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

    Column {
        // Hari dalam minggu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Tanggal-tanggal
        var day = 1
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (i in 0 until 7) {
                    if ((week == 0 && i < firstDayOfMonth) || day > daysInMonth) {
                        Box(modifier = Modifier.size(40.dp).weight(1f))
                    } else {
                        val date = yearMonth.atDay(day)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .weight(1f)
                                .background(
                                    color = when {
                                        date == selectedDate -> MaterialTheme.colorScheme.primary
                                        date == LocalDate.now() -> MaterialTheme.colorScheme.secondaryContainer
                                        absensiList.any { it.tanggal == date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) } -> Color(0xFF8BC34A).copy(alpha = 0.7f)
                                        else -> Color.Transparent
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = when {
                                    date == selectedDate -> Color.White
                                    else -> MaterialTheme.colorScheme.onBackground
                                }
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
private fun AttendanceStats(
    hadir: Int,
    absen: Int,
    izin: Int,
    sakit: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatCard("Hadir", hadir, Color(0xFF4CAF50))
        StatCard("Alpa", absen, Color(0xFFF44336))
        StatCard("Izin", izin, Color(0xFF2196F3))
        StatCard("Sakit", sakit, Color(0xFFFFEB3B))
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                textAlign = TextAlign.Center
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AttendanceItem(
    tanggal: String,
    status: String,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
    val formattedTanggal = LocalDate.parse(tanggal, DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(formatter)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedTanggal,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = status.replaceFirstChar { it.uppercase() },
                color = when (status.lowercase()) {
                    "hadir" -> Color(0xFF4CAF50)
                    "alpa" -> Color(0xFFF44336)
                    "izin" -> Color(0xFF2196F3)
                    "sakit" -> Color(0xFFFFEB3B)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
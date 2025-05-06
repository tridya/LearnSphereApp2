package com.example.learnsphereapp2.ui.orangtua

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(today) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val siswaId = preferencesHelper.getSiswaId()
    Log.d("AbsensiScreenOrangTua", "siswaId dari PreferencesHelper: $siswaId")

    // Validasi siswaId
    if (siswaId == null) {
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

    LaunchedEffect(currentMonth, siswaId) {
        viewModel.fetchAbsensiByStudent(currentMonth.atDay(1))
    }

    LaunchedEffect(absensiList, selectedDate) {
        val dateKey = selectedDate.format(formatter)
        val foundStatus = absensiList.find { it.tanggal == dateKey }?.status
        Log.d("AbsensiScreenOrangTua", "Tanggal dipilih: $dateKey, Status: $foundStatus")
        Log.d("AbsensiScreenOrangTua", "AbsensiList: ${absensiList.map { "${it.tanggal}: ${it.status}" }}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
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

        // Tombol refresh
        Button(
            onClick = { viewModel.fetchAbsensiByStudent(currentMonth.atDay(1)) },
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

        // Pesan error
        errorMessage?.let { pesan ->
            Text(
                text = pesan,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            // Kontrol bulan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    viewModel.fetchAbsensiByStudent(currentMonth.atDay(1))
                }) {
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
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    viewModel.fetchAbsensiByStudent(currentMonth.atDay(1))
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Bulan Berikutnya")
                }
            }

            // Kalender
            CalendarView(
                yearMonth = currentMonth,
                selectedDate = selectedDate,
                absensiList = absensiList,
                onDateSelected = { date -> selectedDate = date }
            )

            // Status absensi
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val dateKey = selectedDate.format(formatter)
                    val status = absensiList.find { it.tanggal == dateKey }?.status
                    if (status != null) {
                        val statusColor = when (status.lowercase()) {
                            "hadir" -> Color(0xFF4CAF50)
                            "alpa" -> Color(0xFFF44336)
                            "izin" -> Color(0xFF2196F3)
                            "sakit" -> Color(0xFFFFEB3B)
                            else -> Color(0xFFDDA0DD)
                        }
                        Text(
                            text = status.replaceFirstChar { it.uppercase() },
                            color = if (status.lowercase() == "sakit") Color.Black else Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(statusColor, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = if (absensiList.isEmpty()) "Tidak ada data absensi untuk bulan ini. Coba refresh atau hubungi guru." else "Tidak ada absensi untuk tanggal ini.",
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    absensiList: List<com.example.learnsphereapp2.data.model.AbsensiResponse>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7 // Minggu = 0, Senin = 1, ..., Sabtu = 6
    val daysOfWeek = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Tanggal-tanggal
        var day = 1
        val totalSlots = (daysInMonth + firstDayOfMonth - 1) / 7 + 1

        for (week in 0 until totalSlots) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (i in 0 until 7) {
                    if (week == 0 && i < firstDayOfMonth || day > daysInMonth) {
                        Box(modifier = Modifier.size(40.dp).weight(1f))
                    } else {
                        val date = yearMonth.atDay(day)
                        val isToday = date == LocalDate.now()
                        val isSelected = date == selectedDate
                        val dateKey = date.format(formatter)
                        val hasAttendance = absensiList.any { it.tanggal == dateKey }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .weight(1f)
                                .background(
                                    color = when {
                                        isSelected -> Color(0xFF6200EE)
                                        isToday -> Color(0xFF6200EE).copy(alpha = 0.7f)
                                        hasAttendance -> Color(0xFF03DAC5).copy(alpha = 0.3f)
                                        else -> Color.Transparent
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    isSelected || isToday -> Color.White
                                    else -> MaterialTheme.colorScheme.onSurface
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
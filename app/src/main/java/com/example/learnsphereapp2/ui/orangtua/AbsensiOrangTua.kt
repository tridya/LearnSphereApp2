package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.guru.SiswaItem
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AbsensiScreenOrangTua(
    navController: NavController,
    siswaId: Int
) {
    // Hardcoded data
    data class AbsensiEntry(val tanggal: String, val status: String)
    val absensiList = listOf(
        AbsensiEntry(tanggal = "2025-03-02", status = "Hadir"),
        AbsensiEntry(tanggal = "2025-03-03", status = "Alpa"),
        AbsensiEntry(tanggal = "2025-03-04", status = "Izin")
    )

    var currentMonth by remember { mutableStateOf(YearMonth.of(2025, 3)) } // March 2025
    val selectedDate = remember { mutableStateOf(LocalDate.of(2025, 3, 2)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Text(
                text = "Absen Harian",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Row {
//                Icon(
//                    imageVector = Icons.Default.CalendarToday,
//                    contentDescription = "Kalender",
//                    modifier = Modifier.size(24.dp)
//                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
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

            item {
                CalendarView(
                    yearMonth = currentMonth,
                    onDateSelected = { date ->
                        selectedDate.value = date
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard("Hadir", absensiList.count { it.status == "Hadir" }, Color(0xFF4CAF50))
                    StatCard("Alpa", absensiList.count { it.status == "Alpa" }, Color(0xFFF44336))
                    StatCard("Izin", absensiList.count { it.status == "Izin" }, Color(0xFF2196F3))
                    StatCard("Sakit", absensiList.count { it.status == "Sakit" }, Color(0xFFFF9800))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (absensiList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada data absensi ditemukan",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(absensiList.size) { index ->
                    val absensi = absensiList[index]
                    SiswaItem(
                        nama = absensi.tanggal,
                        status = absensi.status
                    )
                }
            }
        }
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
                                    if (date == LocalDate.of(2025, 3, 2)) Color(0xFF6200EE) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (date == LocalDate.of(2025, 3, 2)) Color.White else Color.Black
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
fun StatCard(
    title: String,
    count: Int,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}
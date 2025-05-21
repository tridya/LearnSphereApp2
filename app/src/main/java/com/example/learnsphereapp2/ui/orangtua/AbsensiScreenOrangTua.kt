package com.example.learnsphereapp2.ui.orangtua

import android.util.Log
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.learnsphereapp2.data.model.AbsensiResponse
import com.example.learnsphereapp2.data.model.Holiday
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
    val nationalHolidays by viewModel.nationalHolidays
    val isHolidaysLoading by viewModel.isHolidaysLoading

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val siswaIdHardcoded = 1 // Hardcode siswaId di sini
    val currentSiswaId = siswaIdHardcoded

    Log.d("AbsensiScreenOrangTua", "siswaId yang digunakan (hardcoded): $siswaIdHardcoded")
    Log.d("AbsensiScreenOrangTua", "National Holidays saat ini: ${nationalHolidays.map { "${it.date}: ${it.event}" }}")

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
                    text = "Rekapan Absensi Bulanan",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Spacer(modifier = Modifier.width(24.dp))
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

        if (isLoading || isHolidaysLoading) {
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
                    absensiList = absensiList,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Keterangan warna
                ColorLegend()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Statistik absensi
                SectionTitle("Rekapan Bulan ${currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID")))}")
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
            .padding(bottom = 8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Bulan Sebelumnya",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = currentMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
            ),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
        )
        IconButton(
            onClick = onNextMonth,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Bulan Berikutnya",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CalendarView(
    yearMonth: YearMonth,
    absensiList: List<AbsensiResponse>,
    viewModel: AbsensiOrangTuaViewModel
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = if (yearMonth.atDay(1).dayOfWeek.value == 7) 0 else yearMonth.atDay(1).dayOfWeek.value // Pastikan Minggu = 0
    val daysOfWeek = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
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
                            val isToday = date == LocalDate.now()
                            val hasAbsensi = absensiList.any { it.tanggal == date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
                            val isSunday = date.dayOfWeek.value == 7 // Minggu
                            val isNationalHoliday = viewModel.isNationalHoliday(date)
                            val isSemesterHoliday = viewModel.isSemesterHoliday(date)

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        color = when {
                                            isNationalHoliday -> Color(0xFFF44336) // Merah untuk libur nasional
                                            isSemesterHoliday -> Color(0xFFFFEB3B) // Kuning untuk libur semester
                                            isSunday -> Color(0xFFFF9800) // Oranye untuk hari Minggu
                                            isToday && hasAbsensi -> Color(0xFF006FFD) // Biru tua untuk hari ini dengan absensi
                                            isToday -> Color(0xFF006FFD).copy(alpha = 0.3f) // Biru muda untuk hari ini tanpa absensi
                                            hasAbsensi -> Color(0xFF006FFD).copy(alpha = 0.3f) // Biru muda untuk tanggal dengan absensi
                                            else -> Color.Transparent
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = when {
                                        isNationalHoliday || isSemesterHoliday || isSunday || isToday || hasAbsensi -> Color.White
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
}

@Composable
private fun ColorLegend() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        LegendItem(color = Color(0xFFFF9800), label = "Hari Minggu")
        LegendItem(color = Color(0xFFF44336), label = "Libur Nasional")
        LegendItem(color = Color(0xFFFFEB3B), label = "Libur Semester")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
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
    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) color.copy(alpha = 0.2f) else color.copy(alpha = 0.1f)
    )

    Card(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .clickable(
                onClick = { isPressed = !isPressed }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
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
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = color,
                textAlign = TextAlign.Center
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
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
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
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
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
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
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp
        ),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
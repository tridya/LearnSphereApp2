package com.example.learnsphereapp2.ui.guru

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun AbsensiScreenGuru(
    navController: NavController,
    kelasId: Int,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: AbsensiViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AbsensiViewModel(preferencesHelper) as T
            }
        }
    )
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID")) }

    LaunchedEffect(selectedDate) {
        viewModel.fetchData(kelasId = kelasId, tanggal = selectedDate)
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
                    text = "Absen Harian Siswa",
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
                onDateSelected = { date -> selectedDate = date }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Statistik absensi
            AttendanceStats(
                hadir = viewModel.hadirCount.value,
                absen = viewModel.absenCount.value,
                izin = viewModel.izinCount.value,
                sakit = viewModel.sakitCount.value,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            SectionTitle("Daftar Siswa")
            Spacer(modifier = Modifier.height(8.dp))
        }

        when {
            viewModel.isLoading.value -> {
                item {
                    LoadingIndicator()
                }
            }
            viewModel.errorMessage.value != null -> {
                item {
                    ErrorMessage(viewModel.errorMessage.value ?: "Terjadi kesalahan")
                }
            }
            viewModel.siswaList.value.isEmpty() -> {
                item {
                    EmptyState("Tidak ada siswa ditemukan")
                }
            }
            else -> {
                items(viewModel.siswaList.value.sortedBy { it.nama }) { siswa ->
                    val absensi = viewModel.absensiList.value.find { it.siswaId == siswa.siswaId }
                    StudentItem(
                        name = siswa.nama,
                        status = absensi?.status ?: "Belum Diisi",
                        onClick = {
                            navController.navigate(
                                Destinations.ABSENSI_DETAIL_GURU
                                    .replace("{kelasId}", kelasId.toString())
                                    .replace("{tanggal}", selectedDate.format(formatter))
                            )
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
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7 // Minggu = 0, Senin = 1, ..., Sabtu = 6
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

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .weight(1f)
                                .background(
                                    color = when {
                                        isToday && isSelected -> Color(0xFF6200EE)
                                        isToday -> Color(0xFF6200EE).copy(alpha = 0.7f)
                                        isSelected -> Color(0xFF03DAC5)
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
                                    isToday || isSelected -> Color.White
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
private fun StudentItem(
    name: String,
    status: String,
    onClick: () -> Unit
) {
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
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = status,
                color = when (status) {
                    "Hadir" -> Color(0xFF4CAF50)
                    "Alpa" -> Color(0xFFF44336)
                    "Izin" -> Color(0xFF2196F3)
                    "Sakit" -> Color(0xFFFFEB3B)
                    "Belum Diisi" -> MaterialTheme.colorScheme.onSurfaceVariant
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
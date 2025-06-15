package com.example.learnsphereapp2.ui.orangtua

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.data.model.RekapanSiswaResponse
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*
import kotlin.math.max
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun LihatStatistikSiswa(
    navController: NavController,
    preferencesHelper: PreferencesHelper,
    viewModel: OrangTuaViewModel,
    siswaId: Int,
    mataPelajaranId: Int
) {
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    // Fetch reports when the screen is loaded
    LaunchedEffect(siswaId, mataPelajaranId) {
        viewModel.fetchReports(siswaId, mataPelajaranId)
    }

    // Get current date and format it
    val currentDate = LocalDateTime.now()
    val dayOfMonth = currentDate.dayOfMonth
    val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id"))
    val month = currentDate.month.getDisplayName(TextStyle.FULL, Locale("id"))
    val year = currentDate.year

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Header with Back Button and Page Name
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Back Button kiri
                IconButton(
                    onClick = {
                        navController.navigate(Destinations.REKAPAN_SISWA_ORANGTUA) {
                            popUpTo(Destinations.REKAPAN_SISWA_ORANGTUA) { inclusive = false }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BlueCard
                    )
                }

                // Judul tengah
                Text(
                    text = "Statistik Siswa",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkText,
                    modifier = Modifier.align(Alignment.Center)
                )
            }


            // Date and Day Information below
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayOfMonth.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkText
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = dayOfWeek,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = GrayText
                    )
                    Text(
                        text = "$month $year",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = GrayText
                    )
                }
                Text(
                    text = "HARI INI",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = BlueCard
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp)) // Keeps the bar chart lowered

        // Bar Chart
        DynamicBarChart(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Subject header with separator line
        val subjectName = subjects.find { it.mataPelajaranId == mataPelajaranId }?.nama ?: "Mata Pelajaran Tidak Ditemukan"
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)) {
            drawLine(
                color = GrayText,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subjectName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Loading and Error States
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            error != null -> {
                Text(
                    text = error ?: "Terjadi kesalahan",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            reports.isEmpty() -> {
                Text(
                    text = "Tidak ada rekapan untuk mata pelajaran ini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }
            else -> {
                // Reports List
                LazyColumn {
                    items(reports) { report ->
                        ReportCard(report = report)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicBarChart(
    viewModel: OrangTuaViewModel,
    modifier: Modifier = Modifier
) {
    val reports = viewModel.reports.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value

    // Proses data untuk bar chart: jumlah rating per kategori
    val barData = reports.groupBy { report -> report.rating }
        .map { (rating, reports) ->
            Pair(rating, reports.size.toFloat()) // Rating sebagai label, jumlah sebagai nilai
        }
        .sortedBy { it.first } // Urutkan berdasarkan rating untuk konsistensi

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        if (isLoading) {
            Text(
                "Memuat data chart...",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText
            )
        } else if (error != null) {
            Log.e("DynamicBarChart", "Error loading chart data: $error")
            Text(
                text = "Error: $error",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (barData.isEmpty()) {
            Text(
                text = "Tidak ada data untuk chart",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText
            )
        } else {
            BarChart(
                data = barData,
                maxHeight = 150.dp,
                barWidth = 60.dp, // Lebar bar lebih besar untuk label yang lebih panjang
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BarChart(
    data: List<Pair<String, Float>>,
    maxHeight: Dp,
    barWidth: Dp,
    modifier: Modifier = Modifier
) {
    // Tentukan nilai maksimum untuk skala tinggi bar
    val maxValue = data.maxOfOrNull { it.second }?.let { max(it, 1f) } ?: 1f

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(data.size) { index ->
            val (label, value) = data[index]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(barWidth)
            ) {
                // Bar
                Box(
                    modifier = Modifier
                        .height((value / maxValue * maxHeight.value).dp)
                        .width(barWidth)
                        .background(VibrantPurple)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Label (rating, misalnya "Sangat Baik")
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    maxLines = 2, // Dukung label panjang
                    color = DarkText
                )
                // Jumlah
                Text(
                    text = "${value.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = GrayText
                )
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: RekapanSiswaResponse
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Date on the left (only day number, e.g., "19")
        val reportDate = report.tanggal.split("-").let { it[2] } // e.g., "19"
        Text(
            text = reportDate,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = GrayText,
            modifier = Modifier
                .width(40.dp) // Reduced width since only day is shown
                .padding(end = 8.dp)
                .align(Alignment.Top)
        )

        // Card content
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VibrantPurple.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(13.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rating: ${report.rating}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = DarkText
                )
                report.catatan?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Catatan: $it",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = DarkText
                    )
                }
            }
        }
    }
}
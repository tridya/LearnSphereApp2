package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper
import androidx.compose.runtime.LaunchedEffect
import com.example.learnsphereapp2.data.model.RekapanSiswaResponse

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

    // Calculate rating statistics
    val ratingStats = reports.groupBy { it.rating }
        .mapValues { it.value.size }
        .toSortedMap()

    // Fetch reports when the screen is loaded
    LaunchedEffect(siswaId, mataPelajaranId) {
        viewModel.fetchReports(siswaId, mataPelajaranId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Header
        Text(
            text = "Rekapan Nilai",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Lihat rekapan nilai untuk siswa dan mata pelajaran yang dipilih",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = GrayText
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                // Statistics Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PurpleGrey80)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Statistik Rating",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ratingStats.forEach { (rating, count) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$rating Bintang",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = DarkText
                                )
                                Text(
                                    text = "$count kali",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = DarkText
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total Penilaian: ${reports.size}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = DarkText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reports List
                LazyColumn {
                    items(reports) { report ->
                        ReportCard(
                            report = report,
                            subjectName = report.mataPelajaran?.nama ?: "Unknown"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: RekapanSiswaResponse,
    subjectName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VibrantOrange)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Mata Pelajaran: $subjectName",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = DarkText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rating: ${report.rating} Bintang",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = DarkText
            )
            Text(
                text = "Tanggal: ${report.tanggal}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = GrayText
            )
            report.catatan?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Catatan: $it",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = GrayText
                )
            }
        }
    }
}
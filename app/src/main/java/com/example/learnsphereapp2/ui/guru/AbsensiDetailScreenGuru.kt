package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.res.painterResource

@Composable
fun AbsensiDetailScreenGuru(
    navController: NavController,
    kelasId: Int,
    tanggal: String,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: AbsensiViewModel = viewModel(factory = AbsensiViewModelFactory<Any>(preferencesHelper))

    val formatterInput = DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale("id", "ID"))
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val tanggalApi = try {
        val date = LocalDate.parse(tanggal, formatterInput)
        date.format(formatterOutput)
    } catch (e: Exception) {
        LocalDate.now().format(formatterOutput)
    }

    LaunchedEffect(Unit) {
        val date = LocalDate.parse(tanggal, formatterInput)
        viewModel.fetchData(kelasId = kelasId, tanggal = date)
    }

    // State untuk query pencarian
    var searchQuery by remember { mutableStateOf("") }

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
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate(Destinations.ABSENSI_GURU.replace("{kelasId}", kelasId.toString())) }
            )
            Text(
                text = tanggal,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.size(24.dp))
        }

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

        // Kolom pencarian dengan warna biru #006FFD
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Nama Siswa") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedBorderColor = Color(0xFF1976D2),
                focusedLabelColor = Color(0xFF1976D2),
                unfocusedLabelColor = Color(0xFF1976D2)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            viewModel.isLoading.value -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            viewModel.errorMessage.value != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.errorMessage.value ?: "Terjadi kesalahan",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            viewModel.siswaList.value.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada siswa ditemukan",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                // Urutkan dan filter daftar siswa
                val sortedSiswaList = viewModel.siswaList.value
                    .sortedBy { it.nama } // Urutkan dari A-Z
                    .filter { it.nama.contains(searchQuery, ignoreCase = true) } // Filter berdasarkan query pencarian

                if (sortedSiswaList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada siswa yang cocok dengan pencarian",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f) // Pastikan LazyColumn mengambil ruang yang tersedia
                    ) {
                        itemsIndexed(sortedSiswaList) { index, siswa ->
                            SiswaStatusItem(
                                index = index,
                                siswa = siswa,
                                status = viewModel.statusMap[siswa.siswaId] ?: "Hadir",
                                onStatusChange = { newStatus ->
                                    viewModel.updateAbsensi(siswa.siswaId, tanggalApi, newStatus)
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Simpan Perubahan
        Button(
            onClick = {
                // Langsung navigasi ke halaman absensi
                navController.navigate(Destinations.ABSENSI_GURU.replace("{kelasId}", kelasId.toString()))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FFD)) // Warna biru #006FFD
        ) {
            Text(
                text = "Simpan Perubahan",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        BottomNavigationGuru(navController)
    }
}

@Composable
fun SiswaStatusItem(
    index: Int,
    siswa: SiswaResponse,
    status: String,
    onStatusChange: (String) -> Unit
) {
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
                text = "${index + 1}. ${siswa.nama}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            StatusIcon(
                status = status,
                onStatusChange = onStatusChange
            )
        }
    }
}

@Composable
fun StatusIcon(
    status: String,
    onStatusChange: (String) -> Unit
) {
    Row {
        Icon(
            painter = painterResource(id = R.drawable.ic_hadir),
            contentDescription = "Hadir",
            tint = if (status == "Hadir") Color(0xFF4CAF50) else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable { onStatusChange("Hadir") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_absen),
            contentDescription = "Alpa",
            tint = if (status == "Alpa") Color(0xFFF44336) else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable { onStatusChange("Alpa") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_izin),
            contentDescription = "Izin",
            tint = if (status == "Izin") Color(0xFF2196F3) else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable { onStatusChange("Izin") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_sakit),
            contentDescription = "Sakit",
            tint = if (status == "Sakit") Color(0xFFFF9800) else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable { onStatusChange("Sakit") }
        )
    }
}
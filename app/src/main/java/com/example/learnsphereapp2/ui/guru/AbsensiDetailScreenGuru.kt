package com.example.learnsphereapp2.ui.guru

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import java.util.Locale

@Composable
fun AbsensiDetailScreenGuru(
    navController: NavController,
    kelasId: Int,
    tanggal: String,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: AbsensiViewModel = viewModel(factory = AbsensiViewModelFactory(preferencesHelper))
    val formatterInput = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Validasi kelasId dengan PreferencesHelper
    val context = LocalContext.current
    val validatedKelasId = preferencesHelper.getKelasId() ?: kelasId
    if (validatedKelasId != kelasId) {
        Log.w("AbsensiDetailScreenGuru", "kelasId from navigation ($kelasId) does not match PreferencesHelper ($validatedKelasId). Using PreferencesHelper value.")
    }
    Log.d("AbsensiDetailScreenGuru", "Using kelasId: $validatedKelasId")

    // Parsing tanggal
    val parsedTanggal = try {
        LocalDate.parse(tanggal, formatterInput)
    } catch (e: Exception) {
        Log.e("AbsensiDetailScreenGuru", "Failed to parse tanggal: $tanggal", e)
        LocalDate.now()
    }
    val tanggalApi = parsedTanggal.format(formatterOutput)

    // State untuk pencarian siswa
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(parsedTanggal) {
        viewModel.fetchData(kelasId = validatedKelasId, tanggal = parsedTanggal)
    }

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
                    .clickable {
                        navController.navigate(Destinations.ABSENSI_GURU.replace("{kelasId}", validatedKelasId.toString()))
                    }
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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Nama Siswa") },
            singleLine = true
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
                val filteredSiswaList = viewModel.siswaList.value.filter {
                    it.nama.lowercase().contains(searchQuery.lowercase())
                }

                if (filteredSiswaList.isEmpty() && searchQuery.isNotEmpty()) {
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
                    LazyColumn {
                        itemsIndexed(filteredSiswaList) { index, siswa ->
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

        Spacer(modifier = Modifier.weight(1f))

        BottomNavigationGuru(navController, validatedKelasId)
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

@Composable
fun BottomNavigationGuru(navController: NavController, kelasId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home),
            contentDescription = "Beranda",
            modifier = Modifier
                .size(24.dp)
                .clickable { navController.navigate(Destinations.HOME_GURU) },
            tint = Color.Gray
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_absensi),
            contentDescription = "Absen",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    navController.navigate(Destinations.ABSENSI_GURU.replace("{kelasId}", kelasId.toString()))
                },
            tint = MaterialTheme.colorScheme.primary
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_nilai),
            contentDescription = "Nilai",
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Nanti tambahkan navigasi ke Nilai */ },
            tint = Color.Gray
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_jadwal),
            contentDescription = "Jadwal",
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Nanti tambahkan navigasi ke Jadwal */ },
            tint = Color.Gray
        )
    }
}
package com.example.learnsphereapp2.ui.guru

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DaftarJadwalScreen(
    navController: NavController,
    kelasId: Int,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: JadwalViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JadwalViewModel(preferencesHelper, kelasId) as T
            }
        }
    )

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Jadwal Saat Ini", "Jadwal Mingguan")

    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm:ss", java.util.Locale("id", "ID"))
    val formattedTime = currentTime.format(formatter)

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
                    .clickable { navController.popBackStack() }
            )
            Text(
                text = "Jadwal Kelas $kelasId",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        when (index) {
                            0 -> viewModel.fetchCurrentJadwal()
                            1 -> viewModel.fetchAllJadwal()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> { // Jadwal Saat Ini
                Column {
                    Text(
                        text = "Waktu saat ini: $formattedTime",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when {
                        viewModel.isLoading.value -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        viewModel.errorMessage.value != null -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = viewModel.errorMessage.value ?: "Terjadi kesalahan",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { viewModel.fetchCurrentJadwal() }) {
                                        Text("Coba Lagi")
                                    }
                                }
                            }
                        }
                        viewModel.currentJadwalList.isEmpty() -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Tidak ada jadwal saat ini untuk Kelas $kelasId",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        else -> {
                            LazyColumn {
                                items(viewModel.currentJadwalList.size) { index ->
                                    val jadwal = viewModel.currentJadwalList[index]
                                    JadwalItem(
                                        hari = jadwal.hari,
                                        jamMulai = jadwal.jamMulai,
                                        jamSelesai = jadwal.jamSelesai,
                                        mataPelajaran = jadwal.mataPelajaran.nama ?: "Unknown",
                                        waliKelas = jadwal.waliKelas.nama
                                    )
                                }
                            }
                        }
                    }
                }
            }
            1 -> { // Jadwal Mingguan
                when {
                    viewModel.isLoading.value -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    viewModel.errorMessage.value != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = viewModel.errorMessage.value ?: "Terjadi kesalahan",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.fetchAllJadwal() }) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }
                    viewModel.allJadwalList.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Tidak ada jadwal untuk Kelas $kelasId",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    else -> {
                        Column {
                            // Tabel untuk Jadwal Mingguan (tanpa tombol Tambah Jadwal)
                            Column {
                                TableHeader()
                                LazyColumn {
                                    items(viewModel.allJadwalList.size) { index ->
                                        val jadwal = viewModel.allJadwalList[index]
                                        TableRow(
                                            hari = jadwal.hari,
                                            jamMulai = jadwal.jamMulai,
                                            jamSelesai = jadwal.jamSelesai,
                                            mataPelajaran = jadwal.mataPelajaran.nama ?: "Unknown",
                                            waliKelas = jadwal.waliKelas.nama,
                                            jadwalId = jadwal.jadwalId,
                                            onDeleteClick = {
                                                viewModel.deleteJadwal(
                                                    jadwalId = jadwal.jadwalId,
                                                    onSuccess = {
                                                        // Jadwal sudah dihapus dari allJadwalList di ViewModel
                                                    },
                                                    onError = { error ->
                                                        viewModel.errorMessage.value = error
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1976D2)) // Warna biru untuk header
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hari",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
        )
        Text(
            text = "Jam Mulai",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
        )
        Text(
            text = "Jam Selesai",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
        )
        Text(
            text = "Mata Pelajaran",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
        )
        Text(
            text = "Wali Kelas",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = Modifier.width(48.dp)) // Space hanya untuk tombol hapus
    }
}

@Composable
fun TableRow(
    hari: String,
    jamMulai: String,
    jamSelesai: String,
    mataPelajaran: String,
    waliKelas: String,
    jadwalId: Int,
    onDeleteClick: () -> Unit // Hanya parameter untuk tombol hapus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE6F0FA)) // Warna latar belakang baris
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = hari,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
        Text(
            text = jamMulai,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
        Text(
            text = jamSelesai,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
        Text(
            text = mataPelajaran,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = waliKelas,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus Jadwal",
                tint = Color.Red
            )
        }
    }
}

@Composable
fun JadwalItem(
    hari: String,
    jamMulai: String,
    jamSelesai: String,
    mataPelajaran: String,
    waliKelas: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$hari, $jamMulai - $jamSelesai",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = mataPelajaran,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Wali Kelas: $waliKelas",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp, color = Color.Gray)
            )
        }
    }
}

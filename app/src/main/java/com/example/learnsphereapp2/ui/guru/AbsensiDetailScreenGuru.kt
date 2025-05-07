package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.foundation.background
import android.util.Log

@Composable
fun AbsensiDetailScreenGuru(
    navController: NavController,
    kelasId: Int,
    tanggal: String,
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

    val formatterInput = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Log.d("AbsensiDetailScreenGuru", "Tanggal diterima: $tanggal")

    val today = LocalDate.now()
    Log.d("AbsensiDetailScreenGuru", "Menggunakan tanggal hari ini secara paksa: $today, Day of week: ${today.dayOfWeek}")

    val parsedDateFromInput = try {
        val date = LocalDate.parse(tanggal, formatterInput)
        Log.d("AbsensiDetailScreenGuru", "Parsed date dari input: $date, Day of week: ${date.dayOfWeek}")
        date
    } catch (e: Exception) {
        Log.e("AbsensiDetailScreenGuru", "Gagal parsing tanggal: $tanggal, error: ${e.message}")
        null
    }

    val parsedDate = today
    val tanggalApi = parsedDate.format(formatterOutput)
    val tanggalDisplay = parsedDate.format(formatterInput)

    LaunchedEffect(Unit) {
        viewModel.fetchData(kelasId = kelasId, tanggal = parsedDate)
    }

    var searchQuery by remember { mutableStateOf("") }
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedSiswaId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                    .clickable {
                        navController.navigate(
                            Destinations.ABSENSI_HARIAN_GURU.replace("{kelasId}", kelasId.toString())
                        )
                    }
            )
            Text(
                text = tanggalDisplay,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusButton("Hadir", Color(0xFF4CAF50))
            StatusButton("Izin", Color(0xFF2196F3))
            StatusButton("Sakit", Color(0xFFFFEB3B))
            StatusButton("Alpa", Color(0xFFF44336))
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = Color(0xFF1976D2)
                )
            },
            placeholder = { Text("Cari Nama Siswa", fontSize = 16.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedBorderColor = Color(0xFF9E9E9E),
                focusedLabelColor = Color(0xFF1976D2),
                unfocusedLabelColor = Color(0xFF9E9E9E)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        viewModel.errorMessage.value?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )
            }
        }

        when {
            viewModel.isLoading.value -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            viewModel.siswaList.value.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada siswa ditemukan",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                val sortedSiswaList = viewModel.siswaList.value
                    .sortedBy { it.nama }
                    .filter { it.nama.contains(searchQuery, ignoreCase = true) }

                if (sortedSiswaList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada siswa yang cocok dengan pencarian",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(sortedSiswaList) { index, siswa ->
                            SiswaStatusItem(
                                index = index,
                                siswa = siswa,
                                status = viewModel.statusMap[siswa.siswaId] ?: "Belum Diisi",
                                onStatusClick = {
                                    selectedSiswaId = siswa.siswaId
                                    showStatusDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(
                    Destinations.ABSENSI_HARIAN_GURU.replace("{kelasId}", kelasId.toString())
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FFD))
        ) {
            Text(
                text = "Simpan Perubahan",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (showStatusDialog && selectedSiswaId != null) {
            val siswaId = selectedSiswaId!!
            AlertDialog(
                onDismissRequest = { showStatusDialog = false },
                title = {
                    Text(
                        text = "Pilih Status Absensi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        text = "Status saat ini: ${viewModel.statusMap[siswaId] ?: "Belum Diisi"}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { showStatusDialog = false }
                    ) {
                        Text("Batal", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.updateAbsensi(siswaId, tanggalApi, "Hadir")
                                showStatusDialog = false
                            }
                        ) {
                            Text("Hadir", fontSize = 16.sp, color = Color(0xFF4CAF50))
                        }
                        TextButton(
                            onClick = {
                                viewModel.updateAbsensi(siswaId, tanggalApi, "Izin")
                                showStatusDialog = false
                            }
                        ) {
                            Text("Izin", fontSize = 16.sp, color = Color(0xFF2196F3))
                        }
                        TextButton(
                            onClick = {
                                viewModel.updateAbsensi(siswaId, tanggalApi, "Sakit")
                                showStatusDialog = false
                            }
                        ) {
                            Text("Sakit", fontSize = 16.sp, color = Color(0xFFFFEB3B))
                        }
                        TextButton(
                            onClick = {
                                viewModel.updateAbsensi(siswaId, tanggalApi, "Alpa")
                                showStatusDialog = false
                            }
                        ) {
                            Text("Alpa", fontSize = 16.sp, color = Color(0xFFF44336))
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun StatusButton(text: String, color: Color) {
    Button(
        onClick = { /* Placeholder */ },
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .height(40.dp)
            .widthIn(min = 70.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            color = if (color == Color(0xFFFFEB3B)) Color.White else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SiswaStatusItem(
    index: Int,
    siswa: SiswaResponse,
    status: String,
    onStatusClick: () -> Unit
) {
    val buttonColor = when (status) {
        "Hadir" -> Color(0xFF4CAF50)
        "Izin" -> Color(0xFF2196F3)
        "Sakit" -> Color(0xFFFFD870)
        "Alpa" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    val buttonText = when (status) {
        "Hadir" -> "Hadir"
        "Izin" -> "Izin"
        "Sakit" -> "Sakit"
        "Alpa" -> "Alpa"
        else -> "Belum Diisi"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FA)), // Warna biru muda seperti di kode asli
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index + 1}. ${siswa.nama}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onStatusClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(32.dp)
                    .width(100.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = buttonText,
                    color = if (status == "Sakit") Color.White else Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
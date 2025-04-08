package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphereapp2.data.model.SiswaDetailResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun SiswaDetailScreen(
    navController: NavController,
    siswaId: Int,
    preferencesHelper: PreferencesHelper
) {
    var siswaDetail by remember { mutableStateOf<SiswaDetailResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = preferencesHelper.getToken() ?: return@LaunchedEffect
        try {
            val response = RetrofitClient.apiService.getSiswaByKode(
                authorization = "Bearer $token",
                kodeSiswa = siswaId.toString()
            )
            if (response.isSuccessful) {
                siswaDetail = response.body()
            } else {
                errorMessage = "Gagal mengambil data: ${response.message()}"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // Ganti CenterHorizontally dengan Alignment.CenterHorizontally
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Terjadi kesalahan",
                color = MaterialTheme.colorScheme.error
            )
        } else if (siswaDetail != null) {
            Text(
                text = "Data Siswa",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nama: ${siswaDetail!!.siswa.nama}")
            Text("Kelas ID: ${siswaDetail!!.siswa.kelasId}")
            Text("Kode Siswa: ${siswaDetail!!.siswa.kodeSiswa}")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Riwayat Absensi",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (siswaDetail!!.absensi.isEmpty()) {
                Text("Belum ada data absensi")
            } else {
                LazyColumn {
                    items(siswaDetail!!.absensi) { absensi ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Tanggal: ${absensi.tanggal}")
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Status: ${absensi.status}")
                            }
                        }
                    }
                }
            }
        }
    }
}
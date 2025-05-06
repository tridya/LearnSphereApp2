package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun HomeScreenOrangTua(
    navController: NavController,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: JadwalOrangTuaViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return JadwalOrangTuaViewModel(preferencesHelper) as T // Hapus siswaId = 0
            }
        }
    )
    val siswaList = viewModel.siswaList.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchSiswaByParent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Selamat datang, ${preferencesHelper.getUsername() ?: "Orang Tua"}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DarkText
                )
                Text(
                    text = "Lihat informasi terkait anak Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NotificationBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tampilkan menu meskipun siswaList kosong, dengan indikasi
        if (siswaList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Tidak ada anak yang terkait dengan akun Anda. Silakan hubungi admin.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            siswaList.forEach { siswa ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Destinations.ABSENSI_ORANGTUA.replace("{siswaId}", siswa.siswaId.toString()))
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VibrantPurple)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Absensi ${siswa.nama}",
                                style = MaterialTheme.typography.titleLarge,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lihat kehadiran ${siswa.nama}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayText
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_absen_ilustration),
                            contentDescription = "Ilustrasi Absensi",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Destinations.JADWAL_ORANGTUA.replace("{siswaId}", siswa.siswaId.toString()))
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VibrantBlue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Jadwal ${siswa.nama}",
                                style = MaterialTheme.typography.titleLarge,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lihat jadwal ${siswa.nama}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayText
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_jadwal_ilustration),
                            contentDescription = "Ilustrasi Jadwal",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Destinations.NILAI_ORANGTUA.replace("{siswaId}", siswa.siswaId.toString()))
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VibrantOrange)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Nilai ${siswa.nama}",
                                style = MaterialTheme.typography.titleLarge,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lihat nilai ${siswa.nama}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayText
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_nilai_ilustration),
                            contentDescription = "Ilustrasi Nilai",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.BackgroundWhite
import com.example.learnsphereapp2.ui.theme.GrayText
import com.example.learnsphereapp2.ui.theme.VibrantBlue
import com.example.learnsphereapp2.ui.theme.VibrantOrange
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun JadwalKegiatanScreen(
    navController: NavController,
    preferencesHelper: PreferencesHelper
) {
    val context = LocalContext.current
    val username = preferencesHelper.getUsername() ?: "Unknown"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.size(24.dp))
                Text(
                    text = "Jadwal Kegiatan",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable {
//                                navController.navigate(Destinations.NOTIFIKASI_GURU) // Mengarah ke rute guru
                            },
                        tint = Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { /* TODO: Aksi profil */ },
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text(
                    text = "Selamat Datang, $username",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black
                )
                Text(
                    text = "Semoga sukses dalam mengelola jadwal hari ini!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { navController.navigate(Destinations.TAMBAH_JADWAL) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantOrange),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Tambah Jadwal",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Text(
                            text = "Buat jadwal baru",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate(Destinations.TAMBAH_JADWAL) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = VibrantOrange
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "TAMBAH",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_absen_ilustration),
                        contentDescription = "Ilustrasi Tambahkan Jadwal Pelajaran",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { navController.navigate(Destinations.DAFTAR_JADWAL) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Lihat Daftar",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Text(
                            text = "Lihat dan kelola jadwal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate(Destinations.DAFTAR_JADWAL) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = VibrantBlue
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "LIHAT",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_jadwal_ilustration),
                        contentDescription = "Ilustrasi Lihat Daftar Pelajaran",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}
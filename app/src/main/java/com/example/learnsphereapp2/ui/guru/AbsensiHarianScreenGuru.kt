package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.foundation.clickable
import com.example.learnsphereapp2.ui.components.CommonTitleBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsensiHarianScreenGuru(
    navController: NavController,
    kelasId: Int,
    preferencesHelper: PreferencesHelper
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Title Bar
            CommonTitleBar(
                title = "Absensi Harian",
                showBackButton = false,
                onProfileClick = { navController.navigate(Destinations.PROFILE_GURU) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card Absen Harian Siswa
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable {
                        navController.navigate(
                            Destinations.ABSENSI_DETAIL_GURU.replace("{kelasId}", kelasId.toString())
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantPurple)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Absen Harian Siswa",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Catat kehadiran siswa harian",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GrayText.copy(alpha = 0.9f)
                            )
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_absenn),
                        contentDescription = "Absen Harian",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card Lihat Absensi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable {
                        val today = LocalDate.now().format(
                            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
                        )
                        navController.navigate(
                            Destinations.ABSENSI_GURU
                                .replace("{kelasId}", kelasId.toString())
                                .replace("{tanggal}", today)
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Lihat Absensi",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lihat rekapan absensi siswa",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GrayText.copy(alpha = 0.9f)
                            )
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_absenn),
                        contentDescription = "Lihat Absensi",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}
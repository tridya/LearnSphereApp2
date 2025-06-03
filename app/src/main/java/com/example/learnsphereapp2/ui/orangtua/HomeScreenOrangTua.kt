package com.example.learnsphereapp2.ui.orangtua

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
import com.example.learnsphereapp2.ui.components.CommonTitleBar
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun HomeScreenOrangTua(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)
    val username = preferencesHelper.getUsername() ?: "Unknown"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        CommonTitleBar(
            title = "Beranda Orang Tua",
            navController = navController,
            showBackButton = false,
            showNotificationIcon = true,
            showProfileIcon = true,
            onNotificationClick = { /* TODO: Aksi notifikasi */ },
            onProfileClick = { navController.navigate(Destinations.PROFILE_ORANGTUA) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Teks sambutan
        Column {
            Text(
                text = "Halo Orang Tua,",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = DarkText
            )
            Text(
                text = "Pantau perkembangan $username di sini",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp
                ),
                color = GrayText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Card Laporan
        FeatureCard(
            title = "Laporan Harian",
            description = "Lihat aktivitas dan kehadiran $username",
            iconRes = R.drawable.ic_absenn,
            color = VibrantPurple,
            onClick = { navController.navigate(Destinations.ABSENSI_ORANGTUA) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card Nilai
        FeatureCard(
            title = "Perkembangan Nilai",
            description = "Pantau perkembangan akademik $username",
            iconRes = R.drawable.ic_nilai_ilustration,
            color = VibrantOrange,
            onClick = { navController.navigate(Destinations.REKAPAN_SISWA_ORANGTUA) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card Jadwal
        FeatureCard(
            title = "Jadwal Pelajaran",
            description = "Lihat jadwal pelajaran $username",
            iconRes = R.drawable.ic_jadwal_ilustration,
            color = VibrantBlue,
            onClick = { navController.navigate(Destinations.JADWAL_ORANGTUA) }
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    iconRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp
                    ),
                    color = GrayText
                )
            }
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}
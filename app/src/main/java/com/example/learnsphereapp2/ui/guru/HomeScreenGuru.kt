package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun HomeScreenGuru(
    navController: NavController
) {
    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)
    val username = preferencesHelper.getUsername() ?: "Unknown"

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
            Column {
                Text(
                    text = "Good day $username",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Here some of the ways you can find help to grow in your studies",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = Color.Gray
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.profile_placeholder), // Ganti dengan ikon notifikasi kalau ada
                contentDescription = "Notifications",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Destinations.ABSENSI_GURU) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FA))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_absensi), // Pastikan ikon ini ada
                    contentDescription = "Absensi",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Absen\nTrack student attendance",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Nilai
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Nanti tambahkan navigasi ke Nilai */ },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_nilai), // Tambahkan ikon nilai di drawable
                    contentDescription = "Nilai",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Nilai\nManage student grades",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Jadwal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Nanti tambahkan navigasi ke Jadwal */ },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6FFE6))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_jadwal), // Pastikan ikon ini ada
                    contentDescription = "Jadwal",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Jadwal\nManage your schedule",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation dengan 4 ikon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home), // Pastikan ikon ini ada
                contentDescription = "Home",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Sudah di Home */ },
                tint = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_absensi), // Pastikan ikon ini ada
                contentDescription = "Absen",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate(Destinations.ABSENSI_GURU) }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_nilai), // Tambahkan ikon nilai di drawable
                contentDescription = "Nilai",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Nanti tambahkan navigasi ke Nilai */ }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_jadwal), // Pastikan ikon ini ada
                contentDescription = "Jadwal",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Nanti tambahkan navigasi ke Jadwal */ }
            )
        }
    }
}
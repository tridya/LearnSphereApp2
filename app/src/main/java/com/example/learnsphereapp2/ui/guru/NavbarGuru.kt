package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@Composable
fun StatCard(title: String, count: Int, color: Color, status: String) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = color
            )
            Text(
                text = "$count Siswa",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = color
            )
        }
    }
}

@Composable
fun BottomNavigationGuru(navController: NavController, selectedScreen: String = "Home") {
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
            tint = if (selectedScreen == "Home") MaterialTheme.colorScheme.primary else Color.Gray
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_absensi),
            contentDescription = "Absen",
            modifier = Modifier
                .size(24.dp)
                .clickable { navController.navigate(Destinations.ABSENSI_GURU.replace("{kelasId}", "1")) },
            tint = if (selectedScreen == "Absensi") MaterialTheme.colorScheme.primary else Color.Gray
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_nilai),
            contentDescription = "Nilai",
            modifier = Modifier.size(24.dp),
            tint = if (selectedScreen == "Nilai") MaterialTheme.colorScheme.primary else Color.Gray
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_jadwal),
            contentDescription = "Jadwal",
            modifier = Modifier
                .size(24.dp)
                .clickable { navController.navigate(Destinations.JADWAL_KEGIATAN) },
            tint = if (selectedScreen == "Jadwal") MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}
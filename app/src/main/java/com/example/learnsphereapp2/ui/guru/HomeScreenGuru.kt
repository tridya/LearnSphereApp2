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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.DarkText
import com.example.learnsphereapp2.ui.theme.GrayText
import com.example.learnsphereapp2.ui.theme.VibrantBlue
import com.example.learnsphereapp2.ui.theme.VibrantOrange
import com.example.learnsphereapp2.ui.theme.VibrantPurple
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header: Judul dan ikon notifikasi/profil
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), // Padding atas untuk header
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Judul "HomeScreen Guru" rata kiri
            Text(
                text = "HomeScreen Guru",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            // Ikon notifikasi dan profil
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { /* TODO: Aksi notifikasi */ },
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1D5DB))
                        .clickable { navController.navigate(Destinations.PROFILE_GURU) },
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Teks sambutan
        Column {
            Text(
                text = "Good day $username,",
                style = MaterialTheme.typography.headlineLarge,
                color = DarkText
            )
            Text(
                text = "Here some of the ways you can find help to grow in your studies",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card Absensi
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Destinations.ABSENSI_HARIAN_GURU) },
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
                        text = "Absensi",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Monitoring kehadiran siswa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayText
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_absenn),
                    contentDescription = "Ilustrasi Absensi",
                    modifier = Modifier.size(80.dp)
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
                        text = "Nilai",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Monitoring nilai siswa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayText
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_nilaii),
                    contentDescription = "Ilustrasi Nilai",
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Jadwal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Destinations.JADWAL_KEGIATAN) },
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
                        text = "Jadwal",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Daftar jadwal mata pelajaran\nsiswa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayText
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_jadwall),
                    contentDescription = "Ilustrasi Jadwal",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}
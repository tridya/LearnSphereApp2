package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.learnsphereapp2.ui.theme.BackgroundWhite
import com.example.learnsphereapp2.ui.theme.BlueCard
import com.example.learnsphereapp2.ui.theme.GrayText
import com.example.learnsphereapp2.ui.theme.YellowCard
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
        // Header: Tombol kembali di kiri, judul di tengah, dan ikon lonceng/profil di kanan
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol kembali di kiri
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { navController.navigate(Destinations.HOME_GURU) },
                tint = Color.Black
            )

            // Judul "Jadwal Kegiatan" di tengah
            Text(
                text = "Jadwal Kegiatan",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )

            // Ikon lonceng dan profil di kanan
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { /* TODO: Aksi notifikasi */ },
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1D5DB))
                        .clickable { /* TODO: Aksi profil */ },
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Salam: "Good Morning, $username" dan "We wish you have a good day"
        Column {
            Text(
                text = "Good Morning, $username",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            Text(
                text = "We wish you have a good day",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Card Data Jadwal Course
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clickable { navController.navigate(Destinations.TAMBAH_JADWAL) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlueCard),
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
                        text = "Data Jadwal\nCourse",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate(Destinations.TAMBAH_JADWAL) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = BlueCard
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
                    painter = painterResource(id = R.drawable.ic_jadwal_daftar),
                    contentDescription = "Ilustrasi Jadwal Course",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Daftar Jadwal Music
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clickable { navController.navigate(Destinations.DAFTAR_JADWAL) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = YellowCard),
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
                        text = "Daftar Jadwal\nMusic",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate(Destinations.DAFTAR_JADWAL) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = YellowCard
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
                    painter = painterResource(id = R.drawable.ic_jadwal_tambah),
                    contentDescription = "Ilustrasi Jadwal Music",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
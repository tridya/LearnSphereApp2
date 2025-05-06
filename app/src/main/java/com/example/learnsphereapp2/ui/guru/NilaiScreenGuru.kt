package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.*

@Composable
fun NilaiScreenGuru(
    navController: NavController
) {
    // Data dummy untuk daftar siswa
    val siswaList = List(7) { "Azzahra Anisa" }

    // State untuk search query
    val searchQuery = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header: Judul, ikon notifikasi, dan profil
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Daftar Nilai",
                style = MaterialTheme.typography.headlineLarge,
                color = DarkText
            )
            Row(
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
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(NotificationBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "T",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subjudul
        Text(
            text = "MASUKKAN NILAI\nDAFTAR NILAI - APR 30 - PAUSE PRACTICE",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkText,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = GrayText
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = DarkText),
                    decorationBox = { innerTextField ->
                        if (searchQuery.value.isEmpty()) {
                            Text(
                                text = "Cari Nama Siswa",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayText
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilterButton(text = "Sangat Baik", color = BlueCard)
            FilterButton(text = "Baik", color = FilterGreen)
            FilterButton(text = "Cukup", color = FilterOrange)
            FilterButton(text = "Kurang", color = FilterRed)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daftar Siswa
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(siswaList) { index, siswa ->
                SiswaNilaiItem(index + 1, siswa)
                Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Siswa dan Tombol Simpan
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${siswaList.size} SISWA",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText
            )
            Button(
                onClick = { /* TODO: Simpan data */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueCard,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(40.dp)
                    .width(100.dp)
            ) {
                Text(
                    text = "Simpan",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun FilterButton(text: String, color: Color) {
    Button(
        onClick = { /* TODO: Filter logic */ },
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(36.dp)
            .width(90.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
        )
    }
}

@Composable
fun SiswaNilaiItem(index: Int, nama: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$index.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkText,
                modifier = Modifier.width(24.dp)
            )
            Text(
                text = nama,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkText
            )
        }
        Button(
            onClick = { /* TODO: Navigasi ke detail nilai */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE5E7EB),
                contentColor = DarkText
            ),
            shape = CircleShape,
            modifier = Modifier
                .height(36.dp)
                .width(80.dp)
        ) {
            Text(
                text = "Nilai",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
            )
        }
    }
}
package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations // Impor Destinations
import com.example.learnsphereapp2.ui.guru.JadwalItem
import com.example.learnsphereapp2.ui.orangtua.FilterBar
import com.example.learnsphereapp2.ui.orangtua.FilterItem
import com.example.learnsphereapp2.ui.orangtua.FilterType
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle // Impor TextStyle
import java.util.Locale
import android.util.Log
import com.example.learnsphereapp2.ui.components.CommonTitleBar

@Composable
fun JadwalOrangTuaScreen(
    navController: NavController,
    siswaId: Int,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: JadwalOrangTuaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JadwalOrangTuaViewModel(preferencesHelper, siswaId) as T
            }
        }
    )

    val currentTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale("id", "ID"))
    val formattedDate = currentTime.format(dateFormatter)
    val formattedTime = currentTime.format(timeFormatter)
    val currentDay = currentTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID")).replaceFirstChar { it.uppercase() }

    // State for filter selection
    var selectedFilter by remember {
        mutableStateOf(
            FilterItem(
                type = FilterType.hari_ini,
                label = "Hari Ini",
                iconRes = FilterType.hari_ini.iconRes,
                selectedColor = FilterType.hari_ini.selectedColor,
                selectedText = FilterType.hari_ini.selectedText,
                selectedIconColor = FilterType.hari_ini.selectedIconColor
            )
        )
    }
    val filters = listOf(
        FilterItem(
            type = FilterType.hari_ini,
            label = "Hari Ini",
            iconRes = FilterType.hari_ini.iconRes,
            selectedColor = FilterType.hari_ini.selectedColor,
            selectedText = FilterType.hari_ini.selectedText,
            selectedIconColor = FilterType.hari_ini.selectedIconColor
        ),
        FilterItem(
            type = FilterType.jadwal_ini,
            label = "Keseluruhan Jadwal",
            iconRes = FilterType.jadwal_ini.iconRes,
            selectedColor = FilterType.jadwal_ini.selectedColor,
            selectedText = FilterType.jadwal_ini.selectedText,
            selectedIconColor = FilterType.jadwal_ini.selectedIconColor
        )
    )

    LaunchedEffect(siswaId) {
        Log.d("JadwalOrangTuaScreen", "Fetching data for siswaId: $siswaId")
        if (siswaId > 0) {
            viewModel.fetchCurrentJadwal(siswaId)
            viewModel.fetchAllJadwal(siswaId)
        } else {
            Log.e("JadwalOrangTuaScreen", "Invalid siswaId: $siswaId")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Header using CommonTitleBar
        CommonTitleBar(
            title = "Jadwal Anak",
            navController = navController,
            showBackButton = false,
            showNotificationIcon = true,
            showProfileIcon = true,
            onProfileClick = { /* TODO: Aksi profil */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                color = Color.Black
            )
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Hari Ini: $currentDay",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = GrayText
                )
                Text(
                    text = "Waktu: $formattedTime",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = GrayText
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Filter Bar
        FilterBar(
            filters = filters,
            selectedFilter = selectedFilter,
            onFilterSelected = { filter ->
                selectedFilter = filter
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Konten Jadwal
        val jadwalList by remember { derivedStateOf {
            when (selectedFilter.type) {
                FilterType.hari_ini -> viewModel.allJadwalList.filter { jadwal ->
                    jadwal.hari?.equals(currentDay, ignoreCase = true) == true
                }
                FilterType.jadwal_ini -> viewModel.allJadwalList
                else -> viewModel.allJadwalList
            }
        } }

        when {
            viewModel.isLoading.value -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            viewModel.errorMessage.value != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = viewModel.errorMessage.value ?: "Terjadi kesalahan",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.fetchCurrentJadwal(siswaId)
                            viewModel.fetchAllJadwal(siswaId)
                        }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            jadwalList.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = when (selectedFilter.type) {
                            FilterType.hari_ini -> "Tidak ada jadwal untuk hari $currentDay."
                            FilterType.jadwal_ini -> "Tidak ada jadwal untuk anak ini."
                            else -> "Tidak ada jadwal untuk anak ini."
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                LazyColumn {
                    items(jadwalList.size) { index ->
                        val jadwal = jadwalList[index]
                        JadwalItem(
                            jamMulai = jadwal.jamMulai ?: "Unknown",
                            jamSelesai = jadwal.jamSelesai ?: "Unknown",
                            mataPelajaran = jadwal.mataPelajaran?.nama ?: "Unknown",
                            waliKelas = jadwal.waliKelas?.nama ?: "Unknown",
                            cardColor = getCardColor(index),
                            showActions = false
                        )
                    }
                }
            }
        }
    }
}

fun getCardColor(index: Int): Color {
    val colors = listOf(VibrantPurple, VibrantOrange, VibrantBlue)
    return colors[index % colors.size]
}
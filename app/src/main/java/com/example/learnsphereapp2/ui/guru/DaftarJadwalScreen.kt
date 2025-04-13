@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DaftarJadwalScreen(
    navController: NavController,
    kelasId: Int,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: JadwalViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JadwalViewModel(preferencesHelper, kelasId) as T
            }
        }
    )

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Jadwal Hari Ini", "Jadwal Mingguan")
    val currentTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale("id", "ID"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale("id", "ID"))
    val formattedDate = currentTime.format(dateFormatter)
    val formattedTime = currentTime.format(timeFormatter)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Konten utama diberi weight agar navbar tidak menutupi
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate(Destinations.JADWAL_KEGIATAN) },
                    tint = Color.Black
                )
                Text(
                    text = "Daftar Jadwal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(2f)
                )
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
                            .clickable { },
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
                            .clickable { },
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black
                )
                Text(
                    text = "Hari Ini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
                Text(
                    text = "Waktu Saat Ini: $formattedTime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title, style = MaterialTheme.typography.bodyLarge) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            when (index) {
                                0 -> viewModel.fetchCurrentJadwal()
                                1 -> viewModel.fetchAllJadwal()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> JadwalContent(
                    viewModel = viewModel,
                    kelasId = kelasId,
                    isCurrent = true
                )
                1 -> JadwalContent(
                    viewModel = viewModel,
                    kelasId = kelasId,
                    isCurrent = false,
                    navController = navController
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("tambahJadwal/$kelasId") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueCard,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Tambah Jadwal", style = MaterialTheme.typography.labelLarge)
            }
        }

        // Tambahkan navbar di bagian bawah
        Spacer(modifier = Modifier.height(16.dp))
        BottomNavigationGuru(navController, selectedScreen = "Jadwal")
    }
}

@Composable
fun JadwalContent(
    viewModel: JadwalViewModel,
    kelasId: Int,
    isCurrent: Boolean,
    navController: NavController? = null
) {
    var searchHari by remember { mutableStateOf("") }
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    var expandedHari by remember { mutableStateOf(false) }

    val jadwalList = if (isCurrent) viewModel.currentJadwalList else {
        val sortedList = viewModel.allJadwalList.sortedBy { hariList.indexOf(it.hari ?: "Senin") }
        if (searchHari.isEmpty()) sortedList
        else sortedList.filter { it.hari?.equals(searchHari, ignoreCase = true) == true }
    }

    Column {
        if (!isCurrent) {
            Box {
                OutlinedTextField(
                    value = searchHari,
                    onValueChange = { searchHari = it },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    placeholder = { Text("Cari berdasarkan hari", style = MaterialTheme.typography.bodyLarge, color = GrayText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueCard,
                        unfocusedBorderColor = GrayText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari) }
                )
                DropdownMenu(
                    expanded = expandedHari,
                    onDismissRequest = { expandedHari = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    hariList.forEach { hari ->
                        DropdownMenuItem(
                            text = { Text(hari, style = MaterialTheme.typography.bodyLarge) },
                            onClick = {
                                searchHari = hari
                                expandedHari = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Semua Hari", style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            searchHari = ""
                            expandedHari = false
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expandedHari = true }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                        Button(onClick = { if (isCurrent) viewModel.fetchCurrentJadwal() else viewModel.fetchAllJadwal() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            jadwalList.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isCurrent) "Tidak ada jadwal saat ini untuk Kelas $kelasId"
                        else if (searchHari.isEmpty()) "Tidak ada jadwal untuk Kelas $kelasId"
                        else "Tidak ada jadwal untuk hari $searchHari",
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
                            showActions = !isCurrent,
                            onEditClick = { navController?.navigate("tambahJadwal/$kelasId/${jadwal.jadwalId}") },
                            onDeleteClick = {
                                viewModel.deleteJadwal(
                                    jadwalId = jadwal.jadwalId,
                                    onSuccess = {},
                                    onError = { error -> viewModel.errorMessage.value = error }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JadwalItem(
    jamMulai: String,
    jamSelesai: String,
    mataPelajaran: String,
    waliKelas: String,
    cardColor: Color,
    showActions: Boolean = false,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = jamMulai,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                color = Color.Black
            )
            Text(
                text = jamSelesai,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = GrayText
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier
                .weight(1f)
                .height(80.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mataPelajaran,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = Color.Black
                    )
                    Text(
                        text = "Perkenalan",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = GrayText
                    )
                    Text(
                        text = waliKelas,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = GrayText
                    )
                }
                if (showActions) {
                    Row {
                        IconButton(onClick = { onEditClick?.invoke() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Jadwal",
                                tint = BlueCard
                            )
                        }
                        IconButton(onClick = { onDeleteClick?.invoke() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Jadwal",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getCardColor(index: Int): Color {
    val colors = listOf(Purple80, PurpleGrey80, YellowCard, Pink80)
    return colors[index % colors.size]
}
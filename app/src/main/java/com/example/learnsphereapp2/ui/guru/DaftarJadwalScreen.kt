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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.orangtua.FilterBar
import com.example.learnsphereapp2.ui.orangtua.FilterItem
import com.example.learnsphereapp2.ui.orangtua.FilterType
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DaftarJadwalScreen(
    navController: NavController,
    kelasId: Int? = null,
    preferencesHelper: PreferencesHelper
) {
    val viewModel: JadwalViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JadwalViewModel(preferencesHelper, kelasId ?: -1) as T
            }
        }
    )

    val currentTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale("id", "ID"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale("id", "ID"))
    val formattedDate = currentTime.format(dateFormatter)
    val formattedTime = currentTime.format(timeFormatter)
    val currentDay = currentTime.dayOfWeek.getDisplayName(TextStyle.FULL, java.util.Locale("id", "ID")).replaceFirstChar { it.uppercase() }

    // State for class selection
    val kelasList by viewModel.kelasList.collectAsState()
    var selectedKelasId by remember { mutableStateOf(kelasId ?: -1) } // Default ke -1 jika null
    var expandedKelas by remember { mutableStateOf(false) }

    // State for filter selection
    var selectedFilter by remember {
        mutableStateOf(
            FilterItem(
                type = FilterType.saat_ini,
                label = FilterType.saat_ini.label,
                iconRes = FilterType.saat_ini.iconRes,
                selectedColor = FilterType.saat_ini.selectedColor,
                selectedText = FilterType.saat_ini.selectedText,
                selectedIconColor = FilterType.saat_ini.selectedIconColor
            )
        )
    }
    val filters = listOf(
        FilterItem(
            type = FilterType.saat_ini,
            label = FilterType.saat_ini.label,
            iconRes = FilterType.saat_ini.iconRes,
            selectedColor = FilterType.saat_ini.selectedColor,
            selectedText = FilterType.saat_ini.selectedText,
            selectedIconColor = FilterType.saat_ini.selectedIconColor
        ),
        FilterItem(
            type = FilterType.hari_ini,
            label = FilterType.hari_ini.label,
            iconRes = FilterType.hari_ini.iconRes,
            selectedColor = FilterType.hari_ini.selectedColor,
            selectedText = FilterType.hari_ini.selectedText,
            selectedIconColor = FilterType.hari_ini.selectedIconColor
        ),
        FilterItem(
            type = FilterType.jadwal_ini,
            label = FilterType.jadwal_ini.label,
            iconRes = FilterType.jadwal_ini.iconRes,
            selectedColor = FilterType.jadwal_ini.selectedColor,
            selectedText = FilterType.jadwal_ini.selectedText,
            selectedIconColor = FilterType.jadwal_ini.selectedIconColor
        )
    )

    LaunchedEffect(Unit) {
        viewModel.fetchKelasByTeacher()
        if (kelasId != null) selectedKelasId = kelasId // Pastikan selectedKelasId diatur dari parameter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Header dengan judul "Jadwal Pelajaran", tombol back, notifikasi, dan profil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
                    tint = DarkText
                )
                Text(
                    text = "Jadwal Pelajaran",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold), // Sedikit semibold
                    color = DarkText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tata letak tanggal di kiri, hari dan jam di kanan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                    color = DarkText
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

            if (kelasList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Anda tidak memiliki kelas yang diasuh.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (selectedKelasId == -1) { // Periksa jika belum ada kelas yang dipilih
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Silakan pilih kelas terlebih dahulu.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                FilterBar(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        when (filter.type) {
                            FilterType.saat_ini -> viewModel.fetchCurrentJadwal()
                            FilterType.hari_ini, FilterType.jadwal_ini -> viewModel.fetchAllJadwal()
                            else -> viewModel.fetchAllJadwal()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedFilter.type) {
                    FilterType.saat_ini -> JadwalContent(
                        viewModel = viewModel,
                        kelasId = selectedKelasId,
                        isCurrent = true,
                        currentDay = currentDay,
                        selectedFilter = selectedFilter
                    )
                    FilterType.hari_ini, FilterType.jadwal_ini -> JadwalContent(
                        viewModel = viewModel,
                        kelasId = selectedKelasId,
                        isCurrent = false,
                        currentDay = currentDay,
                        navController = navController,
                        selectedFilter = selectedFilter
                    )
                    else -> JadwalContent(
                        viewModel = viewModel,
                        kelasId = selectedKelasId,
                        isCurrent = false,
                        currentDay = currentDay,
                        navController = navController,
                        selectedFilter = selectedFilter
                    )
                }
            }
        }
    }
}

@Composable
fun JadwalContent(
    viewModel: JadwalViewModel,
    kelasId: Int,
    isCurrent: Boolean,
    currentDay: String,
    navController: NavController? = null,
    selectedFilter: FilterItem
) {
    var searchHari by remember { mutableStateOf("") }
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Semua Hari")
    var expandedHari by remember { mutableStateOf(false) }

    val jadwalList = if (isCurrent) {
        viewModel.currentJadwalList
    } else {
        if (selectedFilter.type == FilterType.hari_ini) {
            viewModel.allJadwalList.filter { it.hari?.equals(currentDay, ignoreCase = true) == true }
        } else if (selectedFilter.type == FilterType.jadwal_ini) {
            val sortedList = viewModel.allJadwalList.sortedBy { hariList.indexOf(it.hari ?: "Senin") }
            if (searchHari.isEmpty() || searchHari == "Semua Hari") sortedList
            else sortedList.filter { it.hari?.equals(searchHari, ignoreCase = true) == true }
        } else {
            viewModel.allJadwalList
        }
    }

    Column {
        if (!isCurrent && selectedFilter.type == FilterType.jadwal_ini) {
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
                        text = if (isCurrent) {
                            "Tidak ada jadwal saat ini untuk Kelas $kelasId"
                        } else if (selectedFilter.type == FilterType.hari_ini) {
                            "Tidak ada jadwal untuk hari $currentDay"
                        } else if (selectedFilter.type == FilterType.jadwal_ini && searchHari.isNotEmpty() && searchHari != "Semua Hari") {
                            "Tidak ada jadwal untuk hari $searchHari"
                        } else {
                            "Tidak ada jadwal untuk Kelas $kelasId"
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
                            waliKelas = "",
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
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 12.sp),
                color = Color.Black
            )
            Text(
                text = jamSelesai,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                color = GrayText
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
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
                }
                if (showActions) {
                    Row {
                        IconButton(onClick = { onEditClick?.invoke() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Jadwal",
                                tint = VibrantOrange
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
    val colors = listOf(BlueCard, VibrantBlue, WhiteBlue)
    return colors[index % colors.size]
}
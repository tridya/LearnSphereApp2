package com.example.learnsphereapp2.ui.guru

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.data.model.JadwalResponse
import com.example.learnsphereapp2.data.model.MataPelajaranResponse
import com.example.learnsphereapp2.data.model.RekapanSiswaCreate
import com.example.learnsphereapp2.data.model.StatusRekapanSiswa
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RekapanSiswaGuruScreen(
    navController: NavController,
    kelasId: Int,
    preferencesHelper: PreferencesHelper
) {
    val token = preferencesHelper.getToken() ?: ""
    val guruId = preferencesHelper.getUserId() ?: -1

    val rekapanViewModel: RekapanViewModel = viewModel(
        factory = RekapanViewModelFactory(RetrofitClient.apiService, preferencesHelper)
    )

    val jadwalList by rekapanViewModel.jadwalList.collectAsState()
    val mataPelajaranList by rekapanViewModel.mataPelajaran.collectAsState()
    val rekapanList by rekapanViewModel.rekapanList.collectAsState()
    val errorMessage by rekapanViewModel.errorMessage.collectAsState()
    val isLoading by rekapanViewModel.isLoading.collectAsState()
    val siswaList by rekapanViewModel.siswaList.collectAsState()
    val navigateToLogin by rekapanViewModel.navigateToLogin.collectAsState()

    var selectedMataPelajaran by remember { mutableStateOf<MataPelajaranResponse?>(null) }
    var expandedMataPelajaran by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filterRating by remember { mutableStateOf<String?>(null) }

    var showRatingSheet by remember { mutableStateOf(false) }
    var currentSiswaId by remember { mutableStateOf(0) }
    var tempCatatan by remember { mutableStateOf("") }
    var tempRating by remember { mutableStateOf<String?>(null) }
    var showSuccessPopup by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ratingOptions = listOf("Sangat Baik", "Baik", "Cukup", "Kurang")

    LaunchedEffect(Unit) {
        rekapanViewModel.fetchCurrentUser(token)
        rekapanViewModel.fetchStudentsByClass(kelasId, token)
        rekapanViewModel.fetchJadwalByKelas(kelasId, token)
        rekapanViewModel.fetchMataPelajaran(token)
    }

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            preferencesHelper.clear()
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            rekapanViewModel.resetNavigation()
        }
    }

    LaunchedEffect(mataPelajaranList) {
        if (mataPelajaranList.isNotEmpty() && selectedMataPelajaran == null) {
            selectedMataPelajaran = mataPelajaranList.first()
            rekapanViewModel.fetchRekapanByKelas(kelasId, selectedMataPelajaran!!.mataPelajaranId, token)
        }
    }

    LaunchedEffect(selectedMataPelajaran) {
        selectedMataPelajaran?.let {
            rekapanViewModel.fetchRekapanByKelas(kelasId, it.mataPelajaranId, token)
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Tutup",
                    duration = SnackbarDuration.Long
                )
                rekapanViewModel.clearErrorMessage()
            }
        }
    }

    val filteredRekapanList = rekapanList.filter { rekapan ->
        val matchesSearch = searchQuery.isEmpty() || rekapan.namaSiswa.lowercase().contains(searchQuery.lowercase())
        val matchesFilter = filterRating == null || (rekapan.sudahDibuat && rekapan.rekapan?.rating == filterRating)
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Nilai") },
                actions = {
                    TextButton(onClick = {
                        preferencesHelper.clear()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }) {
                        Text("Logout")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = "Masukkan Nilai",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5E6CC))
                ) {
                    Text(
                        text = "Daftar Nilai\n",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color.Gray
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Cari Nama Siswa", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ratingOptions.forEach { rating ->
                        FilterChip(
                            selected = filterRating == rating,
                            onClick = { filterRating = if (filterRating == rating) null else rating },
                            label = { Text(rating, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = when (rating) {
                                    "Sangat Baik" -> Color(0xFF2196F3)
                                    "Baik" -> Color(0xFF4CAF50)
                                    "Cukup" -> Color(0xFFFF9800)
                                    "Kurang" -> Color(0xFFF44336)
                                    else -> Color(0xFFCCCCCC)
                                },
                                labelColor = Color.White
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                var isFocused by remember { mutableStateOf(false) }
                val isDropdownEnabled = mataPelajaranList.isNotEmpty()
                OutlinedTextField(
                    value = selectedMataPelajaran?.nama ?: "Pilih Mata Pelajaran",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(enabled = isDropdownEnabled) { expandedMataPelajaran = true }
                        .onFocusChanged { isFocused = it.isFocused },
                    readOnly = true,
                    enabled = isDropdownEnabled,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    placeholder = { Text("Pilih Mata Pelajaran", fontSize = 12.sp) },
                    trailingIcon = {
                        if (isDropdownEnabled) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.clickable { expandedMataPelajaran = true }
                            )
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF00BCD4),
                        unfocusedBorderColor = Color(0xFF03A9F4),
                        disabledBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                DropdownMenu(
                    expanded = expandedMataPelajaran && isDropdownEnabled,
                    onDismissRequest = { expandedMataPelajaran = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    mataPelajaranList.forEach { mp ->
                        DropdownMenuItem(
                            text = { Text(mp.nama, fontSize = 12.sp) },
                            onClick = {
                                selectedMataPelajaran = mp
                                expandedMataPelajaran = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                errorMessage != null -> {
                    item {
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
                siswaList.isEmpty() -> {
                    item {
                        Text(
                            text = "Tidak ada siswa ditemukan untuk kelas ini",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
                filteredRekapanList.isEmpty() && selectedMataPelajaran != null -> {
                    item {
                        Text(
                            text = "Tidak ada rekapan ditemukan untuk mata pelajaran ini",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
                else -> {
                    itemsIndexed(filteredRekapanList) { index, rekapan ->
                        val borderColor = if (rekapan.sudahDibuat) {
                            when (rekapan.rekapan?.rating) {
                                "Sangat Baik" -> Color(0xFF2196F3)
                                "Baik" -> Color(0xFF4CAF50)
                                "Cukup" -> Color(0xFFFF9800)
                                "Kurang" -> Color(0xFFF44336)
                                else -> Color(0xFFCCCCCC)
                            }
                        } else {
                            Color(0xFFCCCCCC)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FA))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}. ${rekapan.namaSiswa}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    modifier = Modifier.weight(1f)
                                )
                                if (rekapan.sudahDibuat) {
                                    rekapan.rekapan?.let { r ->
                                        val ratingColor = when (r.rating) {
                                            "Sangat Baik" -> Color(0xFF2196F3)
                                            "Baik" -> Color(0xFF4CAF50)
                                            "Cukup" -> Color(0xFFFF9800)
                                            "Kurang" -> Color(0xFFF44336)
                                            else -> Color(0xFFCCCCCC)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .background(ratingColor, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = r.rating,
                                                color = Color.Black,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                                            )
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            currentSiswaId = rekapan.siswaId
                                            tempRating = null
                                            tempCatatan = ""
                                            showRatingSheet = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(70.dp, 30.dp), // Ukuran diperbesar agar teks tidak terpotong
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Nilai",
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "${filteredRekapanList.size} Siswa",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 10.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showRatingSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRatingSheet = false },
            sheetState = rememberModalBottomSheetState(),
            modifier = Modifier.imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f, fill = false)
            ) {
                Text(
                    text = "Beri Nilai",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Rating",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ratingOptions.forEach { rating ->
                        FilterChip(
                            selected = tempRating == rating,
                            onClick = { tempRating = rating },
                            label = { Text(rating, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = when (rating) {
                                    "Sangat Baik" -> Color(0xFF2196F3)
                                    "Baik" -> Color(0xFF4CAF50)
                                    "Cukup" -> Color(0xFFFF9800)
                                    "Kurang" -> Color(0xFFF44336)
                                    else -> Color(0xFFCCCCCC)
                                },
                                labelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = tempCatatan,
                    onValueChange = { if (it.length <= 200) tempCatatan = it },
                    label = { Text("Catatan (opsional)", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    supportingText = { Text("${tempCatatan.length}/200", fontSize = 10.sp) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showRatingSheet = false }) {
                        Text("Batal", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (tempRating == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Pilih rating terlebih dahulu")
                                }
                            } else if (selectedMataPelajaran == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Pilih mata pelajaran terlebih dahulu")
                                }
                            } else if (guruId <= 0) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("ID guru tidak valid. Silakan login kembali.")
                                }
                            } else {
                                val mataPelajaranId = selectedMataPelajaran?.mataPelajaranId ?: return@Button
                                val rekapanCreate = RekapanSiswaCreate(
                                    siswa_id = currentSiswaId,
                                    mata_pelajaran_id = mataPelajaranId,
                                    guru_id = guruId,
                                    rating = tempRating!!,
                                    catatan = tempCatatan.takeIf { it.isNotBlank() }
                                )
                                rekapanViewModel.createDailyRekapan(rekapanCreate, token) {
                                    selectedMataPelajaran?.let {
                                        rekapanViewModel.fetchRekapanByKelas(kelasId, it.mataPelajaranId, token)
                                    }
                                    showSuccessPopup = true
                                }
                                showRatingSheet = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Simpan", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }

    if (showSuccessPopup) {
        AlertDialog(
            onDismissRequest = { showSuccessPopup = false },
            title = { Text("Sukses", fontSize = 16.sp) },
            text = { Text("Rekapan berhasil disimpan!", fontSize = 12.sp) },
            confirmButton = {
                TextButton(onClick = { showSuccessPopup = false }) {
                    Text("OK", fontSize = 12.sp)
                }
            }
        )
    }
}
package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.BackgroundWhite
import com.example.learnsphereapp2.ui.theme.BlueCard
import com.example.learnsphereapp2.ui.theme.GrayText
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahJadwalScreen(
    navController: NavController,
    preferencesHelper: PreferencesHelper,
    kelasId: Int,
    jadwalId: Int? = null
) {
    val viewModel: JadwalViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JadwalViewModel(preferencesHelper, kelasId) as T
            }
        }
    )

    var selectedHari by remember { mutableStateOf("Senin") }
    var jamMulaiInput by remember { mutableStateOf("") }
    var jamSelesaiInput by remember { mutableStateOf("") }
    var selectedMataPelajaranId by remember { mutableStateOf(1) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val isEditMode = jadwalId != null

    LaunchedEffect(jadwalId) {
        if (jadwalId != null) {
            viewModel.allJadwalList.find { it.jadwalId == jadwalId }?.let {
                selectedHari = it.hari ?: "Senin"
                jamMulaiInput = it.jamMulai ?: ""
                jamSelesaiInput = it.jamSelesai ?: ""
                selectedMataPelajaranId = it.mataPelajaranId ?: 1
            }
        }
    }

    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    var expandedHari by remember { mutableStateOf(false) }
    val mataPelajaranList = listOf(
        Pair(1, "Matematika"), Pair(2, "Bahasa Indonesia"), Pair(3, "IPA"),
        Pair(4, "IPS"), Pair(5, "PPKn"), Pair(6, "Agama"),
        Pair(7, "Seni Budaya"), Pair(8, "Olahraga")
    )
    var expandedMataPelajaran by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f) // Berikan weight agar konten mengisi layar
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
                    text = if (isEditMode) "Edit Jadwal" else "Tambah Jadwal",
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

            Text(
                text = if (isEditMode) "Edit Jadwal" else "Tambah Jadwal",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GrayText, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Pilih Hari",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        OutlinedTextField(
                            value = selectedHari,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            placeholder = { Text("Pilih Hari", style = MaterialTheme.typography.bodyLarge, color = GrayText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BlueCard,
                                unfocusedBorderColor = GrayText
                            ),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari)
                            }
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
                                        selectedHari = hari
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

                    Text(
                        text = "Pilih Jam",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = jamMulaiInput,
                            onValueChange = { jamMulaiInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("08:00", style = MaterialTheme.typography.bodyLarge, color = GrayText) },
                            textStyle = MaterialTheme.typography.bodyLarge,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BlueCard,
                                unfocusedBorderColor = GrayText
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "-",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = jamSelesaiInput,
                            onValueChange = { jamSelesaiInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("10:00", style = MaterialTheme.typography.bodyLarge, color = GrayText) },
                            textStyle = MaterialTheme.typography.bodyLarge,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BlueCard,
                                unfocusedBorderColor = GrayText
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Mata Pelajaran",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        OutlinedTextField(
                            value = mataPelajaranList.find { it.first == selectedMataPelajaranId }?.second
                                ?: "Pilih Mata Pelajaran",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            placeholder = { Text("Matematika", style = MaterialTheme.typography.bodyLarge, color = GrayText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BlueCard,
                                unfocusedBorderColor = GrayText
                            ),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMataPelajaran)
                            }
                        )
                        DropdownMenu(
                            expanded = expandedMataPelajaran,
                            onDismissRequest = { expandedMataPelajaran = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            mataPelajaranList.forEach { (id, nama) ->
                                DropdownMenuItem(
                                    text = { Text(nama, style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        selectedMataPelajaranId = id
                                        expandedMataPelajaran = false
                                    }
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { expandedMataPelajaran = true }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            successMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (jamMulaiInput.isBlank() || jamSelesaiInput.isBlank()) {
                        errorMessage = "Jam mulai dan jam selesai tidak boleh kosong."
                        return@Button
                    }
                    val timeFormatRegex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
                    if (!jamMulaiInput.matches(timeFormatRegex) || !jamSelesaiInput.matches(timeFormatRegex)) {
                        errorMessage = "Format waktu harus HH:MM (contoh: 07:00)."
                        return@Button
                    }

                    if (isEditMode && jadwalId != null) {
                        viewModel.updateJadwal(
                            jadwalId = jadwalId,
                            kelasId = kelasId,
                            hari = selectedHari,
                            jamMulai = jamMulaiInput,
                            jamSelesai = jamSelesaiInput,
                            mataPelajaranId = selectedMataPelajaranId,
                            onSuccess = {
                                successMessage = "Jadwal berhasil diperbarui!"
                                errorMessage = null
                                kotlinx.coroutines.MainScope().launch {
                                    kotlinx.coroutines.delay(2000)
                                    navController.popBackStack()
                                }
                            },
                            onError = { error -> errorMessage = error; successMessage = null }
                        )
                    } else {
                        viewModel.createJadwal(
                            kelasId = kelasId,
                            hari = selectedHari,
                            jamMulai = jamMulaiInput,
                            jamSelesai = jamSelesaiInput,
                            mataPelajaranId = selectedMataPelajaranId,
                            onSuccess = {
                                successMessage = "Jadwal berhasil ditambahkan!"
                                errorMessage = null
                                selectedHari = "Senin"
                                jamMulaiInput = ""
                                jamSelesaiInput = ""
                                selectedMataPelajaranId = 1
                                kotlinx.coroutines.MainScope().launch {
                                    kotlinx.coroutines.delay(2000)
                                    navController.popBackStack()
                                }
                            },
                            onError = { error -> errorMessage = error; successMessage = null }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueCard,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isEditMode) "Simpan Perubahan" else "Tambah",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
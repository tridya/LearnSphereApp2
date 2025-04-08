package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // Tambahkan anotasi ini untuk mengizinkan penggunaan API eksperimental
@Composable
fun TambahJadwalScreen(
    navController: NavController,
    preferencesHelper: PreferencesHelper,
    kelasId: Int, // Tambahkan parameter kelasId
    jadwalId: Int? = null, // Untuk edit (opsional)
    hari: String? = null,
    jamMulai: String? = null,
    jamSelesai: String? = null,
    mataPelajaranId: Int? = null
) {
    val viewModel: JadwalViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JadwalViewModel(preferencesHelper, kelasId) as T
            }
        }
    )

    // State untuk form
    var selectedHari by remember { mutableStateOf(hari ?: "Senin") }
    var jamMulaiInput by remember { mutableStateOf(jamMulai ?: "") }
    var jamSelesaiInput by remember { mutableStateOf(jamSelesai ?: "") }
    var selectedMataPelajaranId by remember { mutableStateOf(mataPelajaranId ?: 1) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Daftar hari
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    var expandedHari by remember { mutableStateOf(false) }

    // Daftar mata pelajaran (hardcoded berdasarkan data yang diberikan)
    val mataPelajaranList = listOf(
        Pair(1, "Matematika"),
        Pair(2, "Bahasa Indonesia"),
        Pair(3, "IPA"),
        Pair(4, "IPS"),
        Pair(5, "PPKn"),
        Pair(6, "Agama"),
        Pair(7, "Seni Budaya"),
        Pair(8, "Olahraga")
    )
    var expandedMataPelajaran by remember { mutableStateOf(false) }

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
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
            Text(
                text = if (jadwalId != null) "Edit Jadwal" else "Tambah Jadwal",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown untuk memilih hari
        Text("Hari", style = MaterialTheme.typography.bodyLarge)
        Box {
            OutlinedTextField(
                value = selectedHari,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
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
                        text = { Text(hari) },
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

        // Input untuk jam mulai
        Text("Jam Mulai (HH:MM:SS)", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = jamMulaiInput,
            onValueChange = { jamMulaiInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Contoh: 07:00:00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input untuk jam selesai
        Text("Jam Selesai (HH:MM:SS)", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = jamSelesaiInput,
            onValueChange = { jamSelesaiInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Contoh: 08:00:00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown untuk memilih mata pelajaran
        Text("Mata Pelajaran", style = MaterialTheme.typography.bodyLarge)
        Box {
            OutlinedTextField(
                value = mataPelajaranList.find { it.first == selectedMataPelajaranId }?.second ?: "Pilih Mata Pelajaran",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
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
                        text = { Text(nama) },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Tampilkan pesan error atau sukses
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        successMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tombol Simpan
        Button(
            onClick = {
                // Validasi input
                if (jamMulaiInput.isBlank() || jamSelesaiInput.isBlank()) {
                    errorMessage = "Jam mulai dan jam selesai tidak boleh kosong."
                    return@Button
                }
                // Validasi format waktu (HH:MM:SS)
                val timeFormatRegex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")
                if (!jamMulaiInput.matches(timeFormatRegex) || !jamSelesaiInput.matches(timeFormatRegex)) {
                    errorMessage = "Format waktu harus HH:MM:SS (contoh: 07:00:00)."
                    return@Button
                }

                // Panggil fungsi createJadwal
                viewModel.createJadwal(
                    kelasId = kelasId,
                    hari = selectedHari,
                    jamMulai = jamMulaiInput,
                    jamSelesai = jamSelesaiInput,
                    mataPelajaranId = selectedMataPelajaranId,
                    onSuccess = {
                        successMessage = "Jadwal berhasil ditambahkan!"
                        errorMessage = null
                        // Reset form
                        selectedHari = "Senin"
                        jamMulaiInput = ""
                        jamSelesaiInput = ""
                        selectedMataPelajaranId = 1
                        // Navigasi kembali setelah 2 detik
                        kotlinx.coroutines.MainScope().launch {
                            kotlinx.coroutines.delay(2000)
                            navController.popBackStack()
                        }
                    },
                    onError = { error ->
                        errorMessage = error
                        successMessage = null
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Jadwal")
        }
    }
}

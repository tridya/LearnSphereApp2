package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.learnsphereapp2.data.model.AbsensiRequest
import com.example.learnsphereapp2.data.repository.AbsensiRepository
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreenGuru(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper = PreferencesHelper(navController.context),
    absensiRepository: AbsensiRepository = AbsensiRepository()
) {
    var siswaId by remember { mutableStateOf("") }
    var kelasId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Hadir") }
    var catatan by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val token = preferencesHelper.getToken() ?: run {
        navController.navigate("login") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome, Guru!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Username: ${preferencesHelper.getUsername() ?: "Unknown"}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Form Absensi
        Text(text = "Tambah Absensi", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = siswaId,
            onValueChange = { siswaId = it },
            label = { Text("Siswa ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = kelasId,
            onValueChange = { kelasId = it },
            label = { Text("Kelas ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown untuk status
        val statusOptions = listOf("Hadir", "Sakit", "Izin", "Alpa")
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = status,
                onValueChange = {},
                label = { Text("Status") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                statusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            status = option
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = catatan,
            onValueChange = { catatan = it },
            label = { Text("Catatan (Opsional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (siswaId.isBlank() || kelasId.isBlank()) {
                    errorMessage = "Siswa ID dan Kelas ID tidak boleh kosong"
                    return@Button
                }

                isLoading = true
                errorMessage = null
                successMessage = null

                val tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val absensiRequest = AbsensiRequest(
                    siswaId = siswaId.toInt(),
                    kelasId = kelasId.toInt(),
                    tanggal = tanggal,
                    status = status,
                    catatan = if (catatan.isBlank()) null else catatan
                )

                kotlinx.coroutines.MainScope().launch {
                    val result = absensiRepository.createAbsensi(token, absensiRequest)
                    isLoading = false
                    if (result.isSuccess) {
                        successMessage = "Absensi berhasil ditambahkan"
                        siswaId = ""
                        kelasId = ""
                        catatan = ""
                    } else {
                        errorMessage = result.exceptionOrNull()?.message ?: "Gagal menambahkan absensi"
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Tambah Absensi")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        successMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                preferencesHelper.clear()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
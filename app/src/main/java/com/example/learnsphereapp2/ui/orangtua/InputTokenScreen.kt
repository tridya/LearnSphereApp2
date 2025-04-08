package com.example.learnsphereapp2.ui.orangtua

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphereapp2.util.PreferencesHelper
import com.example.learnsphereapp2.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun InputTokenScreen(
    navController: NavController,
    preferencesHelper: PreferencesHelper
) {
    var kodeSiswa by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Masukkan Kode Siswa",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = kodeSiswa,
            onValueChange = { kodeSiswa = it },
            label = { Text("Kode Siswa") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (kodeSiswa.isNotEmpty()) {
                        isLoading = true
                        errorMessage = null
                        val token = preferencesHelper.getToken()
                        if (token == null) {
                            errorMessage = "Token tidak ditemukan. Silakan login kembali."
                            isLoading = false
                            return@Button
                        }
                        Log.d("InputTokenScreen", "Token: $token")
                        Log.d("InputTokenScreen", "Kode Siswa: $kodeSiswa")
                        kotlinx.coroutines.GlobalScope.launch {
                            try {
                                val response = RetrofitClient.apiService.getSiswaByKode(
                                    authorization = "Bearer $token",
                                    kodeSiswa = kodeSiswa
                                )
                                Log.d("InputTokenScreen", "Response Code: ${response.code()}")
                                Log.d("InputTokenScreen", "Response Body: ${response.body()}")
                                if (response.isSuccessful) {
                                    val siswaDetail = response.body()
                                    if (siswaDetail != null) {
                                        navController.navigate("siswa_detail/${siswaDetail.siswa.siswaId}")
                                    } else {
                                        errorMessage = "Data siswa tidak ditemukan"
                                    }
                                } else {
                                    errorMessage = when (response.code()) {
                                        401 -> "Autentikasi gagal. Silakan login kembali."
                                        403 -> "Anda tidak memiliki akses untuk melihat data siswa ini."
                                        404 -> "Siswa tidak ditemukan dengan kode tersebut."
                                        else -> "Gagal mengambil data: ${response.message()}"
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("InputTokenScreen", "Error: ${e.message}", e)
                                errorMessage = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        errorMessage = "Kode siswa tidak boleh kosong"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cari Siswa")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
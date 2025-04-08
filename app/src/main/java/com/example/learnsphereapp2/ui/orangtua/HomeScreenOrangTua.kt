package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.data.model.SiswaResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun HomeScreenOrangTua( // Ubah dari HomeScreenOrangtua menjadi HomeScreenOrangTua
    navController: NavController
) {
    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)
    val username = preferencesHelper.getUsername() ?: "Unknown"

    // State untuk daftar siswa yang terkait dengan orang tua
    var siswaList by remember { mutableStateOf<List<SiswaResponse>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Ambil daftar siswa yang terkait dengan orang tua
    LaunchedEffect(Unit) {
        val token = preferencesHelper.getToken() ?: return@LaunchedEffect
        try {
            val response = RetrofitClient.apiService.getSiswaByOrangTua(
                authorization = "Bearer $token"
            )
            if (response.isSuccessful) {
                siswaList = response.body() ?: emptyList()
            } else {
                errorMessage = "Gagal mengambil daftar siswa: ${response.message()}"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }
}
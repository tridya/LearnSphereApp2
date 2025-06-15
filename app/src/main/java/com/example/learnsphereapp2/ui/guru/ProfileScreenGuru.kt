package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable

@Composable
fun ProfileScreenGuru(
    navController: NavController,
    preferencesHelper: PreferencesHelper
) {
    val coroutineScope = rememberCoroutineScope()
    val apiService = RetrofitClient.apiService

    // State untuk data profil
    var username by remember { mutableStateOf(preferencesHelper.getUsername() ?: "TolaToli") }
    var nama by remember { mutableStateOf(preferencesHelper.getNama() ?: "Tola Toloi") }
    var profilePicture by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Warna biru sesuai spesifikasi (#006FFD)
    val customBlue = Color(0xFF006FFD)

    LaunchedEffect(Unit) {
        isLoading = true
        val token = preferencesHelper.getToken()

        if (token == null) {
            errorMessage = "Token tidak ditemukan. Silakan login kembali."
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val userResponse = apiService.getUser("Bearer $token")
            if (userResponse.isSuccessful) {
                userResponse.body()?.let { user ->
                    username = user.username
                    nama = user.nama
                    profilePicture = user.profilePicture
                    preferencesHelper.saveUserData(
                        userId = user.id.toString(),
                        username = user.username,
                        nama = user.nama,
                        role = user.role,
                        kelasId = preferencesHelper.getKelasId().takeIf { it != -1 }
                    )
                }
            } else {
                errorMessage = "Gagal memuat profil: ${userResponse.message()}"
            }
        } catch (e: Exception) {
            errorMessage = "Terjadi kesalahan: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(customBlue)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { navController.navigateUp() }
                    .align(Alignment.CenterStart),
                tint = Color.White
            )
            Text(
                text = "Akun Saya",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = customBlue)
            }
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Terjadi kesalahan",
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto Profil
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .border(2.dp, customBlue, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Foto Profil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = nama,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kotak Informasi Profil
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nama",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = nama,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                ),
                                modifier = Modifier.weight(2f)
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Username",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = username,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                ),
                                modifier = Modifier.weight(2f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Keluar
                Button(
                    onClick = {
                        coroutineScope.launch {
                            preferencesHelper.clear()
                            navController.navigate(Destinations.LOGIN) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Keluar",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
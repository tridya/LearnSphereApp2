//package com.example.learnsphereapp2.ui.guru
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.learnsphereapp2.R
//import com.example.learnsphereapp2.data.model.SiswaResponse
//import com.example.learnsphereapp2.data.model.UserResponse
//import com.example.learnsphereapp2.network.RetrofitClient
//import com.example.learnsphereapp2.ui.Destinations
//import com.example.learnsphereapp2.ui.theme.BackgroundWhite
//import com.example.learnsphereapp2.ui.theme.VibrantBlue
//import com.example.learnsphereapp2.ui.theme.VibrantPurple
//import com.example.learnsphereapp2.util.PreferencesHelper
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileScreenGuru(
//    navController: NavController,
//    preferencesHelper: PreferencesHelper
//) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    val apiService = RetrofitClient.apiService
//
//    // State untuk data profil
//    var username by remember { mutableStateOf(preferencesHelper.getUsername() ?: "Guru") }
//    var nama by remember { mutableStateOf(preferencesHelper.getNama() ?: "Nama Guru") }
//    var isLoading by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(Unit) {
//        isLoading = true
//        val token = preferencesHelper.getToken()
//
//        if (token == null) {
//            errorMessage = "Token tidak ditemukan. Silakan login kembali."
//            isLoading = false
//            return@LaunchedEffect
//        }
//
//        try {
//            val userResponse = apiService.getUser("Bearer $token")
//            if (userResponse.isSuccessful) {
//                userResponse.body()?.let { user ->
//                    username = user.username
//                    nama = user.nama
//                    preferencesHelper.saveUserData(
//                        userId = user.id.toString(),
//                        username = user.username,
//                        nama = user.nama,
//                        role = user.role,
//                        kelasId = preferencesHelper.getKelasId().takeIf { it != -1 }
//                    )
//                }
//            } else {
//                errorMessage = "Gagal memuat profil: ${userResponse.message()}"
//            }
//        } catch (e: Exception) {
//            errorMessage = "Terjadi kesalahan: ${e.message}"
//        } finally {
//            isLoading = false
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(BackgroundWhite)
//            .padding(horizontal = 24.dp, vertical = 16.dp)
//    ) {
//        // Header: Tombol kembali dan judul
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            // Tombol kembali
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = "Kembali",
//                modifier = Modifier
//                    .size(24.dp)
//                    .clip(CircleShape)
//                    .clickable { navController.navigateUp() },
//                tint = Color.Black
//            )
//
//            // Judul "Profil"
//            Text(
//                text = "Profil",
//                style = MaterialTheme.typography.headlineMedium,
//                color = Color.Black,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.weight(1f)
//            )
//
//            // Spacer untuk menjaga keseimbangan layout
//            Spacer(modifier = Modifier.size(24.dp))
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        // Konten Profil
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//        } else if (errorMessage != null) {
//            Text(
//                text = errorMessage ?: "Terjadi kesalahan",
//                color = Color.Red,
//                style = MaterialTheme.typography.bodyLarge,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//        } else {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // Foto Profil
//                Image(
//                    painter = painterResource(id = R.drawable.profile_placeholder),
//                    contentDescription = "Foto Profil",
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clip(CircleShape)
//                        .border(2.dp, VibrantPurple, CircleShape)
//                        .background(Color.White)
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Username
//                Text(
//                    text = "@$username",
//                    style = MaterialTheme.typography.headlineMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 24.sp
//                    ),
//                    color = Color.Black
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Nama
//                Text(
//                    text = nama,
//                    style = MaterialTheme.typography.titleLarge.copy(
//                        fontWeight = FontWeight.Medium,
//                        fontSize = 20.sp
//                    ),
//                    color = Color.Black
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                // Tombol Logout
//                Button(
//                    onClick = {
//                        coroutineScope.launch {
//                            preferencesHelper.clear()
//                            navController.navigate(Destinations.LOGIN) {
//                                popUpTo(navController.graph.startDestinationId) {
//                                    inclusive = true
//                                }
//                            }
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = VibrantBlue,
//                        contentColor = Color.White
//                    ),
//                    shape = RoundedCornerShape(12.dp),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(48.dp)
//                ) {
//                    Text(
//                        text = "Keluar",
//                        style = MaterialTheme.typography.labelLarge,
//                        fontSize = 16.sp
//                    )
//                }
//            }
//        }
//    }
//}
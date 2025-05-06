package com.example.learnsphereapp2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.example.learnsphereapp2.ui.AppNavGraph
import com.example.learnsphereapp2.ui.theme.LearnSphereAppTheme
import com.example.learnsphereapp2.util.PreferencesHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesHelper = PreferencesHelper(this)

        setContent {
            LearnSphereAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Cek apakah onboarding sudah ditampilkan
                    val isOnboardingShown = preferencesHelper.isOnboardingShown()
                    Log.d("MainActivity", "isOnboardingShown: $isOnboardingShown")

                    // State untuk menentukan apakah onboarding selesai
                    var showOnboarding by remember { mutableStateOf(!isOnboardingShown) }

                    if (showOnboarding) {
                        // Tampilkan onboarding jika belum ditampilkan
                        OnboardingScreen(
                            onFinish = {
                                Log.d("MainActivity", "Onboarding selesai")
                                preferencesHelper.setOnboardingShown(true)
                                showOnboarding = false // Sembunyikan onboarding dan lanjut ke AppNavGraph
                            }
                        )
                    } else {
                        // Langsung ke AppNavGraph jika onboarding sudah ditampilkan
                        AppNavGraph(
                            navController = navController,
                            preferencesHelper = preferencesHelper
                        )
                    }
                }
            }
        }
    }

    // Data untuk setiap halaman onboarding
    data class OnboardingPage(
        val title: String,
        val description: String,
        val animationRes: Int
    )

    @Composable
    fun OnboardingScreen(onFinish: () -> Unit) {
        val pages = listOf(
            OnboardingPage(
                title = "Selamat Datang di LearnSphere!",
                description = "Aplikasi pintar untuk menjembatani komunikasi antara guru dan orang tua dalam memantau perkembangan akademik siswa.",
                animationRes = R.raw.onboarding_1 // Ganti dengan nama file JSON di res/raw
            ),
            OnboardingPage(
                title = "Lihat Prestasi dan Aktivitas Anak Setiap Hari",
                description = "Cek nilai, tugas, absensi, dan perilaku siswa langsung dari ponsel Anda.",
                animationRes = R.raw.onboarding_1
            ),
            OnboardingPage(
                title = "Terhubung Tanpa Batas",
                description = "Chat langsung dengan guru untuk diskusi perkembangan anak tanpa harus datang ke sekolah.",
                animationRes = R.raw.onboarding_1
            ),
            OnboardingPage(
                title = "Siap Memulai?",
                description = "Daftar sebagai orang tua atau guru dan mulai pantau perkembangan siswa.",
                animationRes = R.raw.onboarding_1
            )
        )

        var currentPage by remember { mutableStateOf(0) }

        // Load animasi Lottie
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(pages[currentPage].animationRes))
        val progress by animateLottieCompositionAsState(
            composition,
            isPlaying = true,
            iterations = LottieConstants.IterateForever
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animasi Lottie
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Judul
            Text(
                text = pages[currentPage].title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006FFD),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Deskripsi
            Text(
                text = pages[currentPage].description,
                fontSize = 16.sp,
                color = Color(0xFF006FFD),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Indikator halaman (dots)
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (currentPage == index) Color(0xFF006FFD) else Color.Gray,
                                shape = RoundedCornerShape(50)
                            )

                    )
                    if (index < pages.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp)) // Jarak antar dots 30.dp
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol navigasi
            if (currentPage < pages.size - 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Skip",
                        color = Color(0xFF006FFD),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onFinish() }
                    )

                    Button(
                        onClick = { currentPage++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FFD))
                    ) {
                        Text(text = "Next", color = Color.White)
                    }
                }
            } else {
                Button(
                    onClick = { onFinish() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FFD))
                ) {
                    Text(text = "Masuk", color = Color.White)
                }
            }
        }
    }
}
package com.example.learnsphereapp2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.example.learnsphereapp2.ui.AppNavGraph
import com.example.learnsphereapp2.ui.theme.LearnSphereAppTheme
import com.example.learnsphereapp2.util.PreferencesHelper
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivityFCM"
        private const val TOPIC_NAME = "test_topic"
    }

    // Launcher untuk meminta izin notifikasi
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            subscribeToTopic()
        } else {
            Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
            // Lanjutkan ke onboarding meskipun izin ditolak
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesHelper = PreferencesHelper(this)

        // Minta izin notifikasi saat aplikasi dimulai
        askNotificationPermission()

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
                                showOnboarding = false
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

    private fun askNotificationPermission() {
        // Hanya diperlukan untuk API level 33 (Android 13) dan lebih tinggi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Izin sudah diberikan, langganan ke topik
                Log.d(TAG, "Izin notifikasi sudah diberikan.")
                subscribeToTopic()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Tampilkan penjelasan jika pengguna pernah menolak izin
                Log.d(TAG, "Menampilkan alasan untuk izin notifikasi.")
                Toast.makeText(
                    this,
                    "Izin notifikasi diperlukan untuk menerima pembaruan penting.",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Langsung minta izin
                Log.d(TAG, "Meminta izin notifikasi.")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Perangkat di bawah Android 13 tidak perlu izin runtime
            subscribeToTopic()
        }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NAME)
            .addOnCompleteListener { task ->
                var msg = "Berlangganan ke $TOPIC_NAME berhasil"
                if (!task.isSuccessful) {
                    msg = "Gagal berlangganan ke $TOPIC_NAME: ${task.exception?.message}"
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
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
                animationRes = R.raw.onboarding_1
            ),
            OnboardingPage(
                title = "Lihat Prestasi dan Aktivitas Anak Setiap Hari",
                description = "Cek rekapan, absensi, dan jadwal siswa langsung dari ponsel Anda.",
                animationRes = R.raw.onboarding_1
            ),
            OnboardingPage(
                title = "Terhubung Tanpa Batas",
                description = "Chat langsung dengan guru untuk diskusi perkembangan anak tanpa harus datang ke sekolah.",
                animationRes = R.raw.onboarding_1
            ),
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
                        Spacer(modifier = Modifier.width(8.dp))
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
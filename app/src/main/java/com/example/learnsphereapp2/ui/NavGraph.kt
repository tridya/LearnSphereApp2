package com.example.learnsphereapp2.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.learnsphereapp2.ui.guru.BottomNavigationGuru
import com.example.learnsphereapp2.ui.guru.*
import com.example.learnsphereapp2.ui.login.LoginScreen
import com.example.learnsphereapp2.ui.orangtua.BottomNavigationOrangTua
import com.example.learnsphereapp2.ui.orangtua.HomeScreenOrangTua
import com.example.learnsphereapp2.ui.orangtua.JadwalOrangTuaScreen
import com.example.learnsphereapp2.util.PreferencesHelper

object Destinations {
    const val LOGIN = "login"
    const val HOME_GURU = "home_guru"
    const val ABSENSI_GURU = "absensi_guru/{kelasId}"
    const val ABSENSI_DETAIL_GURU = "absensi_detail_guru/{kelasId}/{tanggal}"
    const val NILAI_GURU = "nilai_guru/{kelasId}"
    const val TAMBAH_JADWAL = "tambahJadwal/{kelasId}/{jadwalId}?"
    const val DAFTAR_JADWAL = "daftar_jadwal/{kelasId}"
    const val JADWAL_KEGIATAN = "jadwal_kegiatan"
    const val NOTIFIKASI_GURU = "notifikasi_guru"

    const val HOME_ORANGTUA = "home_orangtua"
    const val ABSENSI_ORANGTUA = "absensi_orangtua/{siswaId}"
    const val NILAI_ORANGTUA = "nilai_orangtua/{siswaId}"
    const val JADWAL_ORANGTUA = "jadwal_orangtua/{siswaId}"
    const val NOTIFIKASI_ORANGTUA = "notifikasi_orangtua"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val userRole = preferencesHelper.getRole()

    val hideNavBarRoutes = listOf(Destinations.LOGIN)

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideNavBarRoutes) {
                when (userRole) {
                    "guru" -> {
                        if (currentRoute == Destinations.HOME_GURU ||
                            currentRoute?.startsWith("absensi_guru") == true ||
                            currentRoute?.startsWith("nilai_guru") == true ||
                            currentRoute?.startsWith("daftar_jadwal") == true ||
                            currentRoute?.startsWith("tambahJadwal") == true ||
                            currentRoute == Destinations.JADWAL_KEGIATAN ||
                            currentRoute == Destinations.NOTIFIKASI_GURU) { // Tambahkan NOTIFIKASI_GURU
                            BottomNavigationGuru(navController = navController, currentRoute = currentRoute)
                        }
                    }
                    "orang_tua" -> {
                        if (currentRoute == Destinations.HOME_ORANGTUA ||
                            currentRoute?.startsWith("jadwal_orangtua") == true ||
                            currentRoute == Destinations.NOTIFIKASI_ORANGTUA) {
                            BottomNavigationOrangTua(navController = navController, currentRoute = currentRoute)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { role ->
                        preferencesHelper.saveRole(role)
                        if (role == "guru") {
                            navController.navigate(Destinations.HOME_GURU) {
                                popUpTo(Destinations.LOGIN) { inclusive = true }
                            }
                        } else if (role == "orang_tua") {
                            navController.navigate(Destinations.HOME_ORANGTUA) {
                                popUpTo(Destinations.LOGIN) { inclusive = true }
                            }
                        }
                    },
                    preferencesHelper = preferencesHelper
                )
            }

            // Halaman untuk Guru
            composable(Destinations.HOME_GURU) {
                if (userRole == "guru") {
                    HomeScreenGuru(navController = navController)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }
            composable(Destinations.ABSENSI_GURU) { backStackEntry ->
                if (userRole == "guru") {
                    val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                    val kelasId = kelasIdString?.toIntOrNull() ?: 1
                    AbsensiScreenGuru(
                        navController = navController,
                        kelasId = kelasId,
                        preferencesHelper = preferencesHelper
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }
            composable(Destinations.ABSENSI_DETAIL_GURU) { backStackEntry ->
                if (userRole == "guru") {
                    val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                    val tanggal = backStackEntry.arguments?.getString("tanggal") ?: ""
                    val kelasId = kelasIdString?.toIntOrNull() ?: 1
                    AbsensiDetailScreenGuru(
                        navController = navController,
                        kelasId = kelasId,
                        tanggal = tanggal,
                        preferencesHelper = preferencesHelper
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }
            composable(Destinations.NILAI_GURU) { backStackEntry ->
                if (userRole == "guru") {
                    val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                    val kelasId = kelasIdString?.toIntOrNull() ?: 1
                    NilaiScreenGuru(
                        navController = navController
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }
            composable(Destinations.TAMBAH_JADWAL) { backStackEntry ->
                if (userRole == "guru") {
                    val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                    val jadwalIdString = backStackEntry.arguments?.getString("jadwalId")
                    val kelasId = kelasIdString?.toIntOrNull() ?: 1
                    val jadwalId = jadwalIdString?.toIntOrNull()
                    TambahJadwalScreen(
                        navController = navController,
                        preferencesHelper = preferencesHelper,
                        kelasId = kelasId,
                        jadwalId = jadwalId
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }
            composable(Destinations.DAFTAR_JADWAL) { backStackEntry ->
                if (userRole == "guru") {
                    val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                    val kelasId = kelasIdString?.toIntOrNull() ?: 1
                    DaftarJadwalScreen(
                        navController = navController,
                        kelasId = kelasId,
                        preferencesHelper = preferencesHelper
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }
            composable(Destinations.JADWAL_KEGIATAN) {
                if (userRole == "guru") {
                    JadwalKegiatanScreen(
                        navController = navController,
                        preferencesHelper = preferencesHelper
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }
            composable(Destinations.NOTIFIKASI_GURU) {
                if (userRole == "guru") {
                    NotifikasiScreen() // Gunakan versi guru
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Guru.")
                    }
                }
            }

            // Halaman untuk Orang Tua
            composable(Destinations.HOME_ORANGTUA) {
                if (userRole == "orang_tua") {
                    HomeScreenOrangTua(navController = navController, preferencesHelper = preferencesHelper)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Orang Tua.")
                    }
                }
            }
            composable(Destinations.JADWAL_ORANGTUA) { backStackEntry ->
                if (userRole == "orang_tua") {
                    val siswaIdString = backStackEntry.arguments?.getString("siswaId")
                    val siswaId = siswaIdString?.toIntOrNull() ?: 0
                    JadwalOrangTuaScreen(
                        navController = navController,
                        siswaId = siswaId,
                        preferencesHelper = preferencesHelper
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Orang Tua.")
                    }
                }
            }
            composable(Destinations.NOTIFIKASI_ORANGTUA) {
                if (userRole == "orang_tua") {
                    NotifikasiScreen() // Gunakan versi orang tua
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Akses Ditolak. Anda bukan Orang Tua.")
                    }
                }
            }
        }
    }
}
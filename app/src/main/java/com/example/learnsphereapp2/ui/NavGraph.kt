package com.example.learnsphereapp2.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.learnsphereapp2.ui.guru.*
import com.example.learnsphereapp2.ui.login.LoginScreen
import com.example.learnsphereapp2.ui.orangtua.HomeScreenOrangTua
import com.example.learnsphereapp2.util.PreferencesHelper

object Destinations {
    const val LOGIN = "login"
    const val HOME_GURU = "home_guru"
    const val ABSENSI_GURU = "absensi_guru/{kelasId}"
    const val ABSENSI_DETAIL_GURU = "absensi_detail_guru/{kelasId}/{tanggal}"
    const val HOME_ORANGTUA = "home_orangtua"
    const val TAMBAH_JADWAL = "tambahJadwal/{kelasId}/{jadwalId}?"
    const val DAFTAR_JADWAL = "daftar_jadwal/{kelasId}"
    const val JADWAL_KEGIATAN = "jadwal_kegiatan"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Halaman yang tidak memerlukan navbar
    val hideNavBarRoutes = listOf(Destinations.LOGIN, Destinations.HOME_ORANGTUA)

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideNavBarRoutes) {
                BottomNavigationGuru(navController = navController, currentRoute = currentRoute)
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
            composable(Destinations.HOME_GURU) {
                HomeScreenGuru(navController = navController)
            }
            composable(Destinations.ABSENSI_GURU) { backStackEntry ->
                val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                val kelasId = kelasIdString?.toIntOrNull() ?: 1
                AbsensiScreenGuru(
                    navController = navController,
                    kelasId = kelasId,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(Destinations.ABSENSI_DETAIL_GURU) { backStackEntry ->
                val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                val tanggal = backStackEntry.arguments?.getString("tanggal") ?: ""
                val kelasId = kelasIdString?.toIntOrNull() ?: 1
                AbsensiDetailScreenGuru(
                    navController = navController,
                    kelasId = kelasId,
                    tanggal = tanggal,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(Destinations.HOME_ORANGTUA) {
                HomeScreenOrangTua(navController = navController)
            }
            composable(Destinations.TAMBAH_JADWAL) { backStackEntry ->
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
            }
            composable(Destinations.DAFTAR_JADWAL) { backStackEntry ->
                val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                val kelasId = kelasIdString?.toIntOrNull() ?: 1
                DaftarJadwalScreen(
                    navController = navController,
                    kelasId = kelasId,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(Destinations.JADWAL_KEGIATAN) {
                JadwalKegiatanScreen(
                    navController = navController,
                    preferencesHelper = preferencesHelper
                )
            }
        }
    }
}


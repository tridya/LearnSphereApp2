package com.example.learnsphereapp2.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.ui.guru.*
import com.example.learnsphereapp2.ui.login.LoginScreen
import com.example.learnsphereapp2.ui.orangtua.*
import com.example.learnsphereapp2.util.PreferencesHelper

object Destinations {
    const val LOGIN = "login"
    const val HOME_GURU = "home_guru"
    const val ABSENSI_GURU = "absensi_guru/{kelasId}"
    const val ABSENSI_DETAIL_GURU = "absensi_detail_guru/{kelasId}/{tanggal}"
    const val ABSENSI_HARIAN_GURU = "absensi_harian_guru/{kelasId}"
    const val PROFILE_GURU = "profile_guru"
    const val HOME_ORANGTUA = "home_orangtua"
    const val TAMBAH_JADWAL = "tambahJadwal/{kelasId}/{jadwalId}?"
    const val DAFTAR_JADWAL = "daftar_jadwal/{kelasId}"
    const val JADWAL_KEGIATAN = "jadwal_kegiatan"
    const val ABSENSI_ORANGTUA = "absensi_orangtua"
    const val NILAI_ORANGTUA = "nilai_orangtua"
    const val JADWAL_ORANGTUA = "jadwal_orangtua"
    const val PROFILE_ORANGTUA = "profile_orangtua"
    const val REKAPAN_SISWA_ORANGTUA = "rekapan_siswa_orangtua"
    const val STATISTIK_SISWA = "statistik_siswa/{siswaId}/{mataPelajaranId}"
    const val REKAPAN_SISWA_GURU = "rekapan_siswa_guru/{kelasId}"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Destinations.LOGIN) {
                val isOrangTuaRoute = currentRoute == Destinations.HOME_ORANGTUA ||
                        currentRoute == Destinations.ABSENSI_ORANGTUA ||
                        currentRoute == Destinations.NILAI_ORANGTUA ||
                        currentRoute == Destinations.JADWAL_ORANGTUA ||
                        currentRoute == Destinations.PROFILE_ORANGTUA ||
                        currentRoute == Destinations.REKAPAN_SISWA_ORANGTUA ||
                        currentRoute?.contains(Destinations.STATISTIK_SISWA.substringBefore("/{siswaId}")) == true
                if (isOrangTuaRoute) {
                    BottomNavigationOrangTua(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                } else {
                    BottomNavigationGuru(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            }
        }
    ) { innerPadding ->
        // Token check
        if (preferencesHelper.getToken() == null && currentRoute != Destinations.LOGIN) {
            navController.navigate(Destinations.LOGIN) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }

        NavHost(
            navController = navController,
            startDestination = Destinations.LOGIN,
            modifier = modifier.padding(innerPadding)
        ) {
            // Login
            composable(Destinations.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { role ->
                        navController.navigate(
                            if (role == "guru") Destinations.HOME_GURU else Destinations.HOME_ORANGTUA
                        ) {
                            popUpTo(Destinations.LOGIN) { inclusive = true }
                        }
                    },
                    preferencesHelper = preferencesHelper
                )
            }

            // Guru Routes
            composable(Destinations.HOME_GURU) {
                HomeScreenGuru(navController = navController)
            }
            composable(
                route = Destinations.ABSENSI_GURU,
                arguments = listOf(navArgument("kelasId") { type = NavType.StringType })
            ) { backStackEntry ->
                val kelasId = backStackEntry.arguments?.getString("kelasId")?.toIntOrNull() ?: 1
                AbsensiScreenGuru(
                    navController = navController,
                    kelasId = kelasId,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(
                route = Destinations.ABSENSI_DETAIL_GURU,
                arguments = listOf(
                    navArgument("kelasId") { type = NavType.StringType },
                    navArgument("tanggal") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val kelasId = backStackEntry.arguments?.getString("kelasId")?.toIntOrNull() ?: 1
                val tanggal = backStackEntry.arguments?.getString("tanggal") ?: ""
                AbsensiDetailScreenGuru(
                    navController = navController,
                    kelasId = kelasId,
                    tanggal = tanggal,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(
                route = Destinations.ABSENSI_HARIAN_GURU,
                arguments = listOf(navArgument("kelasId") { type = NavType.StringType })
            ) { backStackEntry ->
                val kelasId = backStackEntry.arguments?.getString("kelasId")?.toIntOrNull() ?: 1
                AbsensiHarianScreenGuru(
                    navController = navController,
                    kelasId = kelasId,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(Destinations.PROFILE_GURU) {
                ProfileScreenGuru(navController, preferencesHelper)
            }
            composable(
                route = Destinations.REKAPAN_SISWA_GURU,
                arguments = listOf(navArgument("kelasId") { type = NavType.StringType })
            ) { backStackEntry ->
                val kelasId = backStackEntry.arguments?.getString("kelasId")?.toIntOrNull() ?: 1
                RekapanSiswaGuruScreen(
                    navController = navController,
                    kelasId = kelasId,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(
                route = Destinations.TAMBAH_JADWAL,
                arguments = listOf(
                    navArgument("kelasId") { type = NavType.StringType },
                    navArgument("jadwalId") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val kelasId = backStackEntry.arguments?.getString("kelasId")?.toIntOrNull() ?: 1
                val jadwalId = backStackEntry.arguments?.getString("jadwalId")?.toIntOrNull()
                TambahJadwalScreen(
                    navController = navController,
                    preferencesHelper = preferencesHelper,
                    kelasId = kelasId,
                    jadwalId = jadwalId
                )
            }
            composable(
                route = Destinations.DAFTAR_JADWAL,
                arguments = listOf(navArgument("kelasId") { type = NavType.StringType })
            ) { backStackEntry ->
                val kelasId = backStackEntry.arguments?.getString("kelasId")?.toIntOrNull() ?: 1
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

            // Orangtua Routes
            composable(Destinations.HOME_ORANGTUA) {
                HomeScreenOrangTua(navController = navController)
            }
            composable(Destinations.ABSENSI_ORANGTUA) {
                AbsensiScreenOrangTua(
                    navController = navController,
                    preferencesHelper = preferencesHelper
                )
            }
            composable(Destinations.PROFILE_ORANGTUA) {
                ProfileScreenOrangTua(
                    navController = navController,
                    preferencesHelper = PreferencesHelper(LocalContext.current)
                )
            }
            composable(Destinations.JADWAL_ORANGTUA) {
                JadwalScreenOrangTua(navController = navController)
            }
            navigation(
                startDestination = Destinations.REKAPAN_SISWA_ORANGTUA,
                route = Destinations.NILAI_ORANGTUA
            ) {
                composable(Destinations.REKAPAN_SISWA_ORANGTUA) {
                    val viewModel: OrangTuaViewModel = viewModel(
                        factory = OrangTuaViewModelFactory(
                            apiService = RetrofitClient.apiService,
                            preferencesHelper = preferencesHelper
                        )
                    )
                    LihatRekapanSiswaOrangTua(
                        navController = navController,
                        preferencesHelper = preferencesHelper,
                        viewModel = viewModel
                    )
                }
                composable(
                    route = Destinations.STATISTIK_SISWA,
                    arguments = listOf(
                        navArgument("siswaId") { type = NavType.IntType },
                        navArgument("mataPelajaranId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val viewModel: OrangTuaViewModel = viewModel(
                        factory = OrangTuaViewModelFactory(
                            apiService = RetrofitClient.apiService,
                            preferencesHelper = preferencesHelper
                        )
                    )
                    val siswaId = backStackEntry.arguments?.getInt("siswaId")?.takeIf { it > 0 } ?: run {
                        navController.popBackStack(Destinations.REKAPAN_SISWA_ORANGTUA, false)
                        return@composable
                    }
                    val mataPelajaranId = backStackEntry.arguments?.getInt("mataPelajaranId")?.takeIf { it > 0 } ?: run {
                        navController.popBackStack(Destinations.REKAPAN_SISWA_ORANGTUA, false)
                        return@composable
                    }
                    LihatStatistikSiswa(
                        navController = navController,
                        preferencesHelper = preferencesHelper,
                        viewModel = viewModel,
                        siswaId = siswaId,
                        mataPelajaranId = mataPelajaranId
                    )
                }
            }
        }
    }
}
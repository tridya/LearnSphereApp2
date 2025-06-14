package com.example.learnsphereapp2.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    const val JADWAL_ORANGTUA = "jadwal_orangtua/{siswaId}"
    const val PROFILE_ORANGTUA = "profile_orangtua"
    const val REKAPAN_SISWA_ORANGTUA = "rekapan_siswa_orangtua"
    const val STATISTIK_SISWA = "statistik_siswa/{siswaId}/{mataPelajaranId}"
    const val LIHAT_REKAPAN_GURU = "lihat_rekapan_guru/{kelasId}"
    const val REKAPAN_SISWA_GURU = "rekapan_siswa_guru/{kelasId}"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val hideNavBarRoutes = listOf(Destinations.LOGIN, Destinations.HOME_ORANGTUA)
    LaunchedEffect(currentRoute) {
        if (currentRoute != Destinations.LOGIN && preferencesHelper.getToken() == null) {
            if (preferencesHelper.getToken() == null) { // Periksa ulang
                navController.navigate(Destinations.LOGIN) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }
    Scaffold(
        bottomBar = {
            when {
                // Tampilkan navbar orang tua untuk route orang tua
                currentRoute?.startsWith(Destinations.HOME_ORANGTUA) == true ||
                        currentRoute?.startsWith(Destinations.ABSENSI_ORANGTUA) == true ||
                        currentRoute?.startsWith(Destinations.NILAI_ORANGTUA) == true ||
                        currentRoute?.startsWith(Destinations.JADWAL_ORANGTUA) == true -> {
                    BottomNavigationOrangTua(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
                // Tampilkan navbar guru untuk route guru (kecuali login)
                currentRoute !in hideNavBarRoutes -> {
                    BottomNavigationGuru(
                        navController = navController,
                        currentRoute = currentRoute
                    )
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
                        Log.d("AppNavGraph", "Login successful, role: $role")
                        when (role) {
                            "guru" -> {
                                navController.navigate(Destinations.HOME_GURU) {
                                    popUpTo(Destinations.LOGIN) { inclusive = true }
                                }
                                Log.d("AppNavGraph", "Navigating to HOME_GURU")
                            }
                            "orang_tua" -> {
                                navController.navigate(Destinations.HOME_ORANGTUA) {
                                    popUpTo(Destinations.LOGIN) { inclusive = true }
                                }
                                Log.d("AppNavGraph", "Navigating to HOME_ORANGTUA")
                            }
                            else -> {
                                Log.e("AppNavGraph", "Unknown role: $role")
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
            composable(Destinations.ABSENSI_HARIAN_GURU) { backStackEntry ->
                val kelasIdString = backStackEntry.arguments?.getString("kelasId")
                val kelasId = kelasIdString?.toIntOrNull() ?: 1
                AbsensiHarianScreenGuru(
                    navController = navController,
                    kelasId = kelasId,
                    preferencesHelper = preferencesHelper
                )
            }
//            composable(Destinations.PROFILE_GURU) {
//                ProfileScreenGuru(navController, preferencesHelper)
//            }
            composable(Destinations.HOME_ORANGTUA) {
                HomeScreenOrangTua(navController = navController)
            }
            composable(Destinations.ABSENSI_ORANGTUA) {
                AbsensiScreenOrangTua(
                    navController = navController,
                    preferencesHelper = preferencesHelper
                )
            }
//            navigation(
//                startDestination = Destinations.REKAPAN_SISWA_ORANGTUA,
//                route = Destinations.NILAI_ORANGTUA
//            ) {
//                composable(Destinations.REKAPAN_SISWA_ORANGTUA) {
//                    val viewModel: OrangTuaViewModel = viewModel(
//                        factory = OrangTuaViewModelFactory(
//                            apiService = RetrofitClient.apiService,
//                            preferencesHelper = preferencesHelper
//                        )
//                    )
//                    LihatRekapanSiswaOrangTua(
//                        navController = navController,
//                        preferencesHelper = preferencesHelper,
//                        viewModel = viewModel
//                    )
//                }
//                composable(
//                    route = Destinations.STATISTIK_SISWA,
//                    arguments = listOf(
//                        navArgument("siswaId") { type = NavType.IntType },
//                        navArgument("mataPelajaranId") { type = NavType.IntType }
//                    )
//                ) { backStackEntry ->
//                    val viewModel: OrangTuaViewModel = viewModel(
//                        factory = OrangTuaViewModelFactory(
//                            apiService = RetrofitClient.apiService,
//                            preferencesHelper = preferencesHelper
//                        )
//                    )
//                    val siswaId = backStackEntry.arguments?.getInt("siswaId")?.takeIf { it > 0 } ?: run {
//                        navController.popBackStack(Destinations.REKAPAN_SISWA_ORANGTUA, false)
//                        return@composable
//                    }
//                    val mataPelajaranId = backStackEntry.arguments?.getInt("mataPelajaranId")?.takeIf { it > 0 } ?: run {
//                        navController.popBackStack(Destinations.REKAPAN_SISWA_ORANGTUA, false)
//                        return@composable
//                    }
//                    LihatStatistikSiswa(
//                        navController = navController,
//                        preferencesHelper = preferencesHelper,
//                        viewModel = viewModel,
//                        siswaId = siswaId,
//                        mataPelajaranId = mataPelajaranId
//                    )
//                }
//            }
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
                    Log.w("AppNavGraph", "Invalid siswaId, navigating back to REKAPAN_SISWA_ORANGTUA")
                    navController.popBackStack(Destinations.REKAPAN_SISWA_ORANGTUA, false)
                    return@composable
                }
                val mataPelajaranId = backStackEntry.arguments?.getInt("mataPelajaranId")?.takeIf { it > 0 } ?: run {
                    Log.w("AppNavGraph", "Invalid mataPelajaranId, navigating back to REKAPAN_SISWA_ORANGTUA")
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
            composable(Destinations.JADWAL_ORANGTUA) { backStackEntry ->
                val siswaIdString = backStackEntry.arguments?.getString("siswaId")
                val siswaId = siswaIdString?.toIntOrNull() ?: 1
                JadwalOrangTuaScreen(
                    navController = navController,
                    siswaId = siswaId,
                    preferencesHelper = preferencesHelper
                )
            }
//            composable(Destinations.PROFILE_ORANGTUA) {
//                ProfileScreenOrangTua(
//                    navController = navController,
//                    preferencesHelper = PreferencesHelper(LocalContext.current)
//                )
//            }
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
            composable(
                Destinations.REKAPAN_SISWA_GURU,
                arguments = listOf(navArgument("kelasId") { type = NavType.IntType })
            ) { backStackEntry ->
                val kelasId = backStackEntry.arguments?.getInt("kelasId") ?: 1
                RekapanSiswaGuruScreen(
                    navController = navController,
                    kelasId = kelasId,
                    preferencesHelper = preferencesHelper
                )
            }
//            composable(
//                Destinations.REKAPAN_GURU,
//                arguments = listOf(navArgument("kelasId") { type = NavType.IntType })
//            ) { backStackEntry ->
//                val kelasId = backStackEntry.arguments?.getInt("kelasId") ?: 1
//                RekapanSiswaGuruScreen(
//                    navController = navController,
//                    kelasId = kelasId,
//                    preferencesHelper = preferencesHelper
//                )
//            }

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
        }
    }
}
package com.example.learnsphereapp2.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.learnsphereapp2.ui.guru.AbsensiDetailScreenGuru
import com.example.learnsphereapp2.ui.guru.AbsensiScreenGuru
import com.example.learnsphereapp2.ui.guru.HomeScreenGuru
import com.example.learnsphereapp2.ui.login.LoginScreen
import com.example.learnsphereapp2.ui.orangtua.HomeScreenOrangTua
import com.example.learnsphereapp2.util.PreferencesHelper

object Destinations {
    const val LOGIN = "login"
    const val HOME_GURU = "home_guru"
    const val ABSENSI_GURU = "absensi_guru/{kelasId}"
    const val ABSENSI_DETAIL_GURU = "absensi_detail_guru/{kelasId}/{tanggal}"
    const val HOME_ORANGTUA = "home_orangtua"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    preferencesHelper: PreferencesHelper
) {
    // Daftar kelasId yang valid (mapping hardcode berdasarkan kelas)
    val validKelasIds = setOf(1, 2, 3, 4)

    // Fungsi untuk memvalidasi kelasId
    fun validateKelasId(kelasId: Int?): Int {
        if (kelasId == null || kelasId !in validKelasIds) {
            Log.e("AppNavGraph", "Invalid or no kelasId found: $kelasId. Redirecting to LOGIN")
            navController.navigate(Destinations.LOGIN) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            return 0 // Nilai default, tetapi seharusnya tidak digunakan karena redirect
        }
        return kelasId
    }

    NavHost(navController = navController, startDestination = Destinations.LOGIN) {
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
            // Ambil kelasId dari PreferencesHelper
            val kelasId = preferencesHelper.getKelasId()
            val validatedKelasId = validateKelasId(kelasId)
            if (validatedKelasId == 0) return@composable // Redirect sudah dilakukan di validateKelasId

            HomeScreenGuru(
                navController = navController
            )
        }
        composable(Destinations.ABSENSI_GURU) { backStackEntry ->
            // Ambil kelasId dari PreferencesHelper
            val kelasId = preferencesHelper.getKelasId()
            val validatedKelasId = validateKelasId(kelasId)
            if (validatedKelasId == 0) return@composable // Redirect sudah dilakukan di validateKelasId

            Log.d("AppNavGraph", "Navigating to AbsensiScreenGuru with kelasId: $validatedKelasId")
            AbsensiScreenGuru(
                navController = navController,
                kelasId = validatedKelasId,
                preferencesHelper = preferencesHelper
            )
        }
        composable(Destinations.ABSENSI_DETAIL_GURU) { backStackEntry ->
            // Ambil kelasId dari PreferencesHelper
            val kelasId = preferencesHelper.getKelasId()
            val validatedKelasId = validateKelasId(kelasId)
            if (validatedKelasId == 0) return@composable // Redirect sudah dilakukan di validateKelasId

            val tanggal = backStackEntry.arguments?.getString("tanggal") ?: ""

            Log.d("AppNavGraph", "Navigating to AbsensiDetailScreenGuru with kelasId: $validatedKelasId")
            AbsensiDetailScreenGuru(
                navController = navController,
                kelasId = validatedKelasId,
                tanggal = tanggal,
                preferencesHelper = preferencesHelper
            )
        }
        composable(Destinations.HOME_ORANGTUA) {
            HomeScreenOrangTua(navController = navController)
        }
    }
}
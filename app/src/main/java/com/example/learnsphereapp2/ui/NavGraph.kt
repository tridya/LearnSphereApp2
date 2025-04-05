// ui/NavGraph.kt
package com.example.learnsphereapp2.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.learnsphereapp2.ui.guru.HomeScreenGuru
import com.example.learnsphereapp2.ui.guru.AbsensiDetailScreenGuru
import com.example.learnsphereapp2.ui.guru.AbsensiScreenGuru
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
            HomeScreenGuru(navController = navController)
        }
        composable(Destinations.ABSENSI_GURU) { backStackEntry ->
            val kelasIdString = backStackEntry.arguments?.getString("kelasId")
            Log.d("NavGraph", "kelasId received: $kelasIdString")
            val kelasId = try {
                kelasIdString?.toInt() ?: 1
            } catch (e: NumberFormatException) {
                Log.e("NavGraph", "Invalid kelasId: $kelasIdString, using default value 1")
                1
            }
            AbsensiScreenGuru(
                navController = navController,
                kelasId = kelasId,
                preferencesHelper = preferencesHelper
            )
        }
        composable(Destinations.ABSENSI_DETAIL_GURU) { backStackEntry ->
            val kelasIdString = backStackEntry.arguments?.getString("kelasId")
            val tanggal = backStackEntry.arguments?.getString("tanggal") ?: ""
            Log.d("NavGraph", "kelasId received: $kelasIdString, tanggal: $tanggal")
            val kelasId = try {
                kelasIdString?.toInt() ?: 1
            } catch (e: NumberFormatException) {
                Log.e("NavGraph", "Invalid kelasId: $kelasIdString, using default value 1")
                1
            }
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
    }
}
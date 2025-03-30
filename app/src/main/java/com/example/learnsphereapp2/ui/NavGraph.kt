// ui/NavGraph.kt
package com.example.learnsphereapp2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.learnsphereapp2.ui.guru.HomeScreenGuru
import com.example.learnsphereapp2.ui.login.LoginScreen
import com.example.learnsphereapp2.ui.orangtua.HomeScreenOrangTua
import com.example.learnsphereapp2.util.PreferencesHelper

object Destinations {
    const val LOGIN = "login"
    const val HOME_GURU = "home_guru"
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
        composable(Destinations.HOME_ORANGTUA) {
            HomeScreenOrangTua(navController = navController)
        }
    }
}
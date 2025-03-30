// ui/guru/HomeScreenGuru.kt
package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun HomeScreenGuru(
    navController: NavController // Tambahkan NavController sebagai parameter
) {
    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)
    val username = preferencesHelper.getUsername() ?: "Unknown"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome, Guru $username!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                preferencesHelper.clear() // Hapus semua data dari SharedPreferences
                navController.navigate(Destinations.LOGIN) {
                    popUpTo(Destinations.HOME_GURU) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
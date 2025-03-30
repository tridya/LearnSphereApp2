// MainActivity.kt
package com.example.learnsphereapp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.learnsphereapp2.ui.AppNavGraph
import com.example.learnsphereapp2.ui.Destinations
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
                    // Tenant start destination berdasarkan token dan role
                    if (preferencesHelper.getToken() != null) {
                        when (preferencesHelper.getRole()) {
                            "guru" -> Destinations.HOME_GURU
                            "orang_tua" -> Destinations.HOME_ORANGTUA
                            else -> Destinations.LOGIN
                        }
                    } else {
                        Destinations.LOGIN
                    }
                    AppNavGraph(
                        navController = navController,
                        preferencesHelper = preferencesHelper
                    )
                }
            }
        }
    }
}
package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.BackgroundWhite
import com.example.learnsphereapp2.ui.theme.BlueCard
import com.example.learnsphereapp2.ui.theme.GrayText
import com.example.learnsphereapp2.ui.theme.OffWhite

@Composable
fun BottomNavigationOrangTua(navController: NavController, currentRoute: String?) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = OffWhite, // White background from Color.kt
        contentColor = GrayText // Unselected icon and text color
    ) {
        val items = listOf(
            NavItem(
                iconRes = R.drawable.ic_home, // Ganti dengan resource ikon yang sesuai
                label = "Home",
                route = Destinations.HOME_ORANGTUA
            ),
            NavItem(
                iconRes = R.drawable.ic_jadwal, // Ganti dengan resource ikon yang sesuai
                label = "Jadwal",
                route = Destinations.JADWAL_ORANGTUA.replace("{siswaId}", "1") // Default siswaId
            )
        )

        items.forEach { item ->
            val isSelected = currentRoute?.startsWith(item.route) == true
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp) // Size of the circular background
                            .background(
                                color = if (isSelected) BlueCard else Color.Transparent,
                                shape = CircleShape // Circular background for selected item
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) Color.White else GrayText // White for selected, gray for unselected
                        )
                    }
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // White icon for selected
                    unselectedIconColor = GrayText, // Gray for unselected
                    selectedTextColor = BlueCard, // Blue text for selected label
                    unselectedTextColor = GrayText, // Gray for unselected label
                    indicatorColor = Color.Transparent // Remove the default indicator
                )
            )
        }
    }
}

data class NavItem(
    val iconRes: Int,
    val label: String,
    val route: String
)
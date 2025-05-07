package com.example.learnsphereapp2.ui.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import androidx.compose.ui.res.painterResource
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.BackgroundWhite
import com.example.learnsphereapp2.ui.theme.BlueCard
import com.example.learnsphereapp2.ui.theme.GrayText
import com.example.learnsphereapp2.ui.theme.OffWhite
import androidx.compose.foundation.shape.CircleShape

@Composable
fun StatCard(title: String, count: Int, color: Color, status: String) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = color
            )
            Text(
                text = "$count Siswa",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = color
            )
        }
    }
}


@Composable
fun BottomNavigationGuru(navController: NavController, currentRoute: String?) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = OffWhite, // White background from Color.kt
        contentColor = GrayText // Unselected icon and text color
    ) {
        val items = listOf(
            NavItem(
                iconRes = R.drawable.ic_home,
                label = "Home",
                route = Destinations.HOME_GURU
            ),
            NavItem(
                iconRes = R.drawable.ic_absensi,
                label = "Absensi",
                route = Destinations.ABSENSI_HARIAN_GURU
            ),
            NavItem(
                iconRes = R.drawable.ic_nilai,
                label = "Nilai",
                route = Destinations.NILAI_GURU
            ),
            NavItem(
                iconRes = R.drawable.ic_jadwal,
                label = "Jadwal",
                route = Destinations.JADWAL_KEGIATAN
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
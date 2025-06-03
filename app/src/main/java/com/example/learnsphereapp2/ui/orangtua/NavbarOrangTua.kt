package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.BlueCard
import com.example.learnsphereapp2.ui.theme.GrayText

@Composable
fun BottomNavigationOrangTua(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier.height(64.dp),
        containerColor = Color.White,
        contentColor = GrayText
    ) {
        // Daftar item navbar untuk orang tua
        val menuItems = listOf(
            NavItemOrangTua(
                iconRes = R.drawable.ic_home,
                label = "Beranda",
                route = Destinations.HOME_ORANGTUA
            ),
            NavItemOrangTua(
                iconRes = R.drawable.ic_absensi,
                label = "Absensi",
                route = Destinations.ABSENSI_ORANGTUA
            ),
            NavItemOrangTua(
                iconRes = R.drawable.ic_nilai,
                label = "Nilai",
                route = Destinations.REKAPAN_SISWA_ORANGTUA
            ),
            NavItemOrangTua(
                iconRes = R.drawable.ic_jadwal,
                label = "Jadwal",
                route = Destinations.JADWAL_ORANGTUA
            )
        )

        // Menampilkan setiap item navbar
        menuItems.forEach { menuItem ->
            val isSelected = currentRoute == menuItem.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = menuItem.iconRes),
                        contentDescription = menuItem.label,
                        tint = if (isSelected) BlueCard else GrayText
                    )
                },
                label = {
                    Text(
                        text = menuItem.label,
                        color = if (isSelected) BlueCard else GrayText
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        // Navigasi ke route yang dipilih
                        navController.navigate(menuItem.route) {
                            // Bersihkan back stack sampai ke Beranda
                            popUpTo(Destinations.HOME_ORANGTUA) {
                                saveState = true
                            }
                            // Hindari multiple copy dari screen yang sama
                            launchSingleTop = true
                            // Restore state ketika kembali ke screen sebelumnya
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BlueCard,
                    unselectedIconColor = GrayText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// Data class untuk menyimpan informasi item navbar
data class NavItemOrangTua(
    val iconRes: Int,
    val label: String,
    val route: String
)
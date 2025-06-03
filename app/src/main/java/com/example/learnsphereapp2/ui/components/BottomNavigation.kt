package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.theme.*

// Sealed class untuk mendefinisikan tipe filter
sealed class FilterType(val label: String, val iconRes: Int, val selectedColor: Color, val selectedText: Color, val selectedIconColor: Color) {
    object saat_ini : FilterType("Saat Ini", R.drawable.ic_saat_ini, BlueCard, OffWhite, OffWhite)
    object hari_ini : FilterType("Hari Ini", R.drawable.ic_hari_ini, BlueCard, OffWhite, OffWhite)
    object jadwal_ini : FilterType("Jadwal", R.drawable.ic_jadwal_ini, BlueCard, OffWhite, OffWhite)
}

// Data class untuk item filter
data class FilterItem(
    val type: FilterType,
    val label: String,
    val iconRes: Int,
    val selectedColor: Color,
    val selectedText: Color,
    val selectedIconColor: Color
)

// Komponen FilterBar sebagai container untuk filter button dengan background melengkung
@Composable
fun FilterBar(
    filters: List<FilterItem>,
    selectedFilter: FilterItem?,
    onFilterSelected: (FilterItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp) // Mengatur tinggi FilterBar
            .clip(RoundedCornerShape(12.dp)) // Background dengan sudut melengkung
            .background(OffWhite) // Latar belakang menggunakan VibrantBlue saja
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
    ) {
        filters.forEach { filter ->
            FilterButton(
                filter = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

// Komponen FilterButton untuk setiap tombol filter
@Composable
fun FilterButton(
    filter: FilterItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(40.dp) // Mengatur tinggi FilterButton
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) filter.selectedColor else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = filter.iconRes),
            contentDescription = filter.label,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) filter.selectedIconColor else BlueCard
        )
        Text(
            text = filter.label,
            color = if (isSelected) filter.selectedText else BlueCard,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), // Mengurangi ukuran teks menjadi 12.sp
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

// Preview untuk FilterBar
@Preview
@Composable
fun FilterBarPreview() {
    val filters = listOf(
        FilterItem(
            type = FilterType.saat_ini,
            label = FilterType.saat_ini.label,
            iconRes = FilterType.saat_ini.iconRes,
            selectedColor = FilterType.saat_ini.selectedColor,
            selectedText = FilterType.saat_ini.selectedText,
            selectedIconColor = FilterType.saat_ini.selectedIconColor
        ),
        FilterItem(
            type = FilterType.hari_ini,
            label = FilterType.hari_ini.label,
            iconRes = FilterType.hari_ini.iconRes,
            selectedColor = FilterType.hari_ini.selectedColor,
            selectedText = FilterType.hari_ini.selectedText,
            selectedIconColor = FilterType.hari_ini.selectedIconColor
        ),
        FilterItem(
            type = FilterType.jadwal_ini,
            label = FilterType.jadwal_ini.label,
            iconRes = FilterType.jadwal_ini.iconRes,
            selectedColor = FilterType.jadwal_ini.selectedColor,
            selectedText = FilterType.jadwal_ini.selectedText,
            selectedIconColor = FilterType.jadwal_ini.selectedIconColor
        )
    )
    var selectedFilter by remember { mutableStateOf(filters[0]) } // Default ke "Saat Ini"

    FilterBar(
        filters = filters,
        selectedFilter = selectedFilter,
        onFilterSelected = { filter -> selectedFilter = filter }
    )
}
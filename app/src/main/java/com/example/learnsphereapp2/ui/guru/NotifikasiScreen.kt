package com.example.learnsphereapp2.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learnsphereapp2.HolidayViewModel
import com.example.learnsphereapp2.data.model.Holiday

@Composable
fun NotifikasiScreen(
    viewModel: HolidayViewModel = viewModel()
) {
    val holidays by viewModel.holidays.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchHolidays(
            apiKey = "qhMTjEcoMwIxzAus6c4W109Bu7dNwkWS", // Ganti dengan API key Anda
            country = "ID",
            year = 2025
        )
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(holidays) { holiday ->
            Text(
                text = "${holiday.date.iso}: ${holiday.name}",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
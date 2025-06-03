//package com.example.learnsphereapp2.ui
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
////import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.learnsphereapp2.HolidayViewModel
//import com.example.learnsphereapp2.data.model.Holiday
//import com.example.learnsphereapp2.util.PreferencesHelper
//
//@Composable
//fun NotifikasiScreen(
//    navController: NavController,
//    preferencesHelper: PreferencesHelper,
//    viewModel: HolidayViewModel = viewModel()
//) {
//    val holidays by viewModel.holidays.observeAsState(initial = emptyList())
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchHolidays(
//            apiKey = "qhMTjEcoMwIxzAus6c4W109Bu7dNwkWS", // Ganti dengan API key Anda
//            country = "ID",
//            year = 2025
//        )
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        if (holidays.isEmpty()) {
//            Text(text = "Tidak ada notifikasi hari ini.")
//        } else {
//            LazyColumn {
//                items(holidays) { holiday ->
//                    Text(
//                        text = "${holiday.date.iso}: ${holiday.name}",
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                }
//            }
//        }
//    }
//}
package com.example.learnsphereapp2.ui.orangtua

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.ui.theme.*
import com.example.learnsphereapp2.util.PreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LihatRekapanSiswaOrangTua(
    navController: NavController,
    preferencesHelper: PreferencesHelper,
    viewModel: OrangTuaViewModel
) {
    val students by viewModel.students.collectAsState()
    val selectedSiswaId by viewModel.selectedSiswaId.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Header
        Text(
            text = "Pilih Mata Pelajaran",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pilih siswa dan mata pelajaran untuk melihat rekapan",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = GrayText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Student Dropdown
        if (students.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { /* Handle dropdown expansion if needed */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = students.find { it.siswaId == selectedSiswaId }?.nama ?: "Pilih Siswa",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                // Dropdown menu is handled manually for simplicity
                Column {
                    students.forEach { student ->
                        Text(
                            text = student.nama,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedSiswaId(student.siswaId)
                                }
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Tidak ada siswa yang terkait",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading and Error States
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            error != null -> {
                Text(
                    text = error ?: "Terjadi kesalahan",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            subjects.isEmpty() -> {
                Text(
                    text = "Tidak ada mata pelajaran untuk kelas ini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }
            else -> {
                // Subjects List
                LazyColumn {
                    items(subjects) { subject ->
                        FeatureCard(
                            title = subject.nama,
                            description = subject.deskripsi ?: "Tidak ada deskripsi",
                            iconRes = R.drawable.ic_nilai, // Replace with appropriate icon
                            color = VibrantBlue,
                            onClick = {
                                selectedSiswaId?.let { siswaId ->
                                    navController.navigate(
                                        Destinations.ABSENSI_GURU
                                            .replace("{siswaId}", siswaId.toString())
                                            .replace("{mataPelajaranId}", subject.mataPelajaranId.toString())
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    iconRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp
                    ),
                    color = GrayText
                )
            }
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}
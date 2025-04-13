package com.example.learnsphereapp2.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.learnsphereapp2.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val Typography = Typography(
    // Untuk judul "Jadwal Kegiatan" (lebih kecil dari headlineLarge sebelumnya)
    headlineMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp, // Dikurangi dari 24.sp menjadi 20.sp
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Untuk teks "Good Morning, $username" (diperbesar)
    headlineLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp, // Diperbesar dari 24.sp menjadi 28.sp
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    // Untuk teks sekunder seperti "We wish you have a good day"
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    // Untuk teks di card seperti "Data Jadwal Course" (diperbesar)
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, // Diperbesar dari 16.sp menjadi 20.sp
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Untuk teks tombol seperti "TAMBAH" dan "LIHAT"
    labelLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
// app/src/main/java/com/example/learnsphereapp2/network/RetrofitClient.kt
package com.example.learnsphereapp2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http:/192.168.1.10:8004/" // Untuk emulator; ganti dengan IP server jika menggunakan perangkat fisik

    // Base URL untuk Calendarific API
    private const val CALENDARIFIC_BASE_URL = "https://calendarific.com/api/v2/"

    // Instance untuk API lokal
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Instance untuk Calendarific API
    val calendarificApi: CalendarificApi by lazy {
        Retrofit.Builder()
            .baseUrl(CALENDARIFIC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CalendarificApi::class.java)
    }
}
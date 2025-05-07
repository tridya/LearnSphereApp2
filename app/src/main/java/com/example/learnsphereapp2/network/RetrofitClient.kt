package com.example.learnsphereapp2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Base URL untuk API lokal
    private const val LOCAL_BASE_URL = "http://10.0.2.2:8000/" // Ganti dengan IP server atau "http://10.0.2.2:8000/" untuk emulator

    // Base URL untuk Calendarific API
    private const val CALENDARIFIC_BASE_URL = "https://calendarific.com/api/v2/"

    // Instance untuk API lokal
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(LOCAL_BASE_URL)
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
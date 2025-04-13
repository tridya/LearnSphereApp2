// app/src/main/java/com/example/learnsphereapp2/network/RetrofitClient.kt
package com.example.learnsphereapp2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.49:8000/" // Untuk emulator; ganti dengan IP server jika menggunakan perangkat fisik

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
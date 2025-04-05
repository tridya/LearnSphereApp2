package com.example.learnsphereapp2.data.repository

import com.example.learnsphereapp2.data.model.Absensi
import com.example.learnsphereapp2.data.model.AbsensiRequest
import com.example.learnsphereapp2.data.model.ErrorResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class AbsensiRepository {
    suspend fun createAbsensi(token: String, absensi: AbsensiRequest): Result<Absensi> {
        return try {
            val response = RetrofitClient.apiService.createAbsensi("Bearer $token", absensi)
            if (response.isSuccessful) {
                val absensiResponse = response.body()
                if (absensiResponse != null) {
                    Result.success(absensiResponse)
                } else {
                    Result.failure(Exception("Absensi data is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Result.failure(Exception("Failed to create absensi: ${errorResponse?.getErrorMessage() ?: response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    suspend fun getAbsensiByKelas(token: String, kelasId: Int, tanggal: String? = null): Result<List<Absensi>> {
        return try {
            val response = RetrofitClient.apiService.getAbsensiByKelas("Bearer $token", kelasId, tanggal)
            if (response.isSuccessful) {
                val absensiList = response.body()
                if (absensiList != null) {
                    Result.success(absensiList)
                } else {
                    Result.failure(Exception("Absensi list is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Result.failure(Exception("Failed to fetch absensi: ${errorResponse?.getErrorMessage() ?: response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
}
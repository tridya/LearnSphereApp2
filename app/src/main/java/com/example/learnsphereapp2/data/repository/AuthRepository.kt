package com.example.learnsphereapp2.data.repository

import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import com.example.learnsphereapp2.network.ApiService
import com.example.learnsphereapp2.util.PreferencesHelper

class AuthRepository(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) {
    suspend fun login(username: String, password: String): Result<Token> {
        return try {
            val response = apiService.login(username, password)
            if (response.isSuccessful) {
                val token = response.body()
                if (token != null) {
                    preferencesHelper.saveToken(token.accessToken)
                    Result.success(token)
                } else {
                    Result.failure(Exception("Empty token response"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<UserResponse> {
        return try {
            val token = preferencesHelper.getToken() ?: return Result.failure(Exception("No token found"))
            val response = apiService.getUser("Bearer $token")
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    preferencesHelper.saveUserData(
                        userId = user.id,
                        username = user.username,
                        nama = user.nama,
                        role = user.role,
                        kelasId = null // Sesuaikan jika kelasId diperlukan
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("Empty user response"))
                }
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
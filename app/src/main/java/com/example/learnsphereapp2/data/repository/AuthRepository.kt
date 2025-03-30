package com.example.learnsphereapp2.data.repository

import com.example.learnsphereapp2.data.model.ErrorResponse
import com.example.learnsphereapp2.data.model.Token
import com.example.learnsphereapp2.data.model.UserResponse
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.util.PreferencesHelper
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val preferencesHelper: PreferencesHelper) {
    suspend fun login(username: String, password: String): Result<Token> {
        return try {
            val response = RetrofitClient.apiService.login(username, password)
            if (response.isSuccessful) {
                val token = response.body()
                if (token != null) {
                    preferencesHelper.saveToken(token.accessToken)
                    preferencesHelper.saveUsername(username)
                    Result.success(token)
                } else {
                    Result.failure(Exception("Token is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Result.failure(Exception("Login failed: ${errorResponse?.getErrorMessage() ?: response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    suspend fun getCurrentUser(): Result<UserResponse> {
        val token = preferencesHelper.getToken() ?: return Result.failure(Exception("No token found"))
        return try {
            val response = RetrofitClient.apiService.getUser("Bearer $token")
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    preferencesHelper.saveRole(user.role)
                    preferencesHelper.saveUsername(user.username)
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Result.failure(Exception("Failed to fetch user: ${errorResponse?.getErrorMessage() ?: response.message()}"))
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
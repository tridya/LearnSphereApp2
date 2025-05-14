package com.example.learnsphereapp2.ui.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.repository.AuthRepository
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {
    private val _username = mutableStateOf("")
    val username: String get() = _username.value

    private val _password = mutableStateOf("")
    val password: String get() = _password.value

    private val _usernameError = mutableStateOf<String?>(null)
    val usernameError: State<String?> = _usernameError

    private val _passwordError = mutableStateOf<String?>(null)
    val passwordError: State<String?> = _passwordError

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _loginSuccess = mutableStateOf(false)
    val loginSuccess: State<Boolean> = _loginSuccess

    init {
        // Clear previous session data
        preferencesHelper.clear()
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
        _usernameError.value = if (newUsername.isBlank()) "Username cannot be empty" else null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = if (newPassword.isBlank()) "Password cannot be empty" else null
    }

    fun isInputValid(): Boolean {
        return _username.value.isNotBlank() && _password.value.isNotBlank()
    }

    fun login() {
        if (!isInputValid()) {
            _usernameError.value = if (_username.value.isBlank()) "Username cannot be empty" else null
            _passwordError.value = if (_password.value.isBlank()) "Password cannot be empty" else null
            _errorMessage.value = "Please fill in all fields"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _loginSuccess.value = false

        viewModelScope.launch {
            try {
                val loginResult = authRepository.login(_username.value, _password.value)
                if (loginResult.isSuccess) {
                    val userResult = authRepository.getCurrentUser()
                    if (userResult.isSuccess) {
                        val user = userResult.getOrNull()
                        if (user != null && user.role.isNotBlank()) {
                            _loginSuccess.value = true
                            Log.d("LoginViewModel", "Login successful, user: $user")
                        } else {
                            _errorMessage.value = "Invalid user data or role not found"
                            Log.e("LoginViewModel", "User data invalid or role missing: $user")
                            _loginSuccess.value = false
                        }
                    } else {
                        val error = userResult.exceptionOrNull()
                        _errorMessage.value = when (error) {
                            is HttpException -> when (error.code()) {
                                401 -> "Unauthorized: Invalid token"
                                404 -> "User data not found"
                                else -> "Failed to fetch user data: ${error.message()}"
                            }
                            is ConnectException -> "Cannot connect to server. Check your network."
                            is SocketTimeoutException -> "Connection timed out. Try again later."
                            is IOException -> "Network error. Check your internet connection."
                            else -> "Failed to fetch user data: ${error?.message ?: "Unknown error"}"
                        }
                        Log.e("LoginViewModel", "User fetch failed: ${_errorMessage.value}", error)
                        _loginSuccess.value = false
                    }
                } else {
                    val error = loginResult.exceptionOrNull()
                    _errorMessage.value = when (error) {
                        is HttpException -> when (error.code()) {
                            401 -> "Username or password is incorrect"
                            400 -> "Invalid login request"
                            else -> "Login failed: ${error.message()}"
                        }
                        is ConnectException -> "Cannot connect to server. Check your network."
                        is SocketTimeoutException -> "Connection timed out. Try again later."
                        is IOException -> "Network error. Check your internet connection."
                        else -> "Login failed: ${error?.message ?: "Unknown error"}"
                    }
                    Log.e("LoginViewModel", "Login failed: ${_errorMessage.value}", error)
                    _loginSuccess.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.message ?: "Unknown error"}"
                Log.e("LoginViewModel", "Unexpected error during login", e)
                _loginSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }
}
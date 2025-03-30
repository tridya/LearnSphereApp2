// ui/login/LoginViewModel.kt
package com.example.learnsphereapp2.ui.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.repository.AuthRepository
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferencesHelper: PreferencesHelper // Tambahkan PreferencesHelper sebagai parameter
) : ViewModel() {
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _errorMessage: MutableState<String?> = mutableStateOf(null)
    val errorMessage: State<String?> get() = _errorMessage

    private val _loginSuccess: MutableState<Boolean> = mutableStateOf(false)
    val loginSuccess: State<Boolean> get() = _loginSuccess

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            _errorMessage.value = "Username and password cannot be empty"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _loginSuccess.value = false

        viewModelScope.launch {
            val loginResult = authRepository.login(username, password)
            if (loginResult.isSuccess) {
                val userResult = authRepository.getCurrentUser()
                if (userResult.isSuccess) {
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = userResult.exceptionOrNull()?.message ?: "Failed to fetch user data"
                    _loginSuccess.value = false
                }
            } else {
                _errorMessage.value = loginResult.exceptionOrNull()?.message ?: "Login failed"
                _loginSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    // Fungsi baru untuk menangani navigasi setelah login
    fun handleLoginNavigation(onLoginSuccess: (String) -> Unit) {
        if (_loginSuccess.value) {
            val role = preferencesHelper.getRole()
            if (role != null) {
                onLoginSuccess(role)
                _loginSuccess.value = false // Reset loginSuccess setelah navigasi
            } else {
                _errorMessage.value = "Role not found. Please try again."
                _loginSuccess.value = false
            }
        }
    }
}
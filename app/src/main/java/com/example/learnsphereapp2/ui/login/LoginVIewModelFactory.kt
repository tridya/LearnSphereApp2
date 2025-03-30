// ui/login/LoginViewModelFactory.kt
package com.example.learnsphereapp2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.learnsphereapp2.data.repository.AuthRepository
import com.example.learnsphereapp2.util.PreferencesHelper

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val preferencesHelper: PreferencesHelper // Tambahkan parameter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository, preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
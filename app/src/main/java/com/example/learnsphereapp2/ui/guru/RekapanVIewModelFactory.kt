package com.example.learnsphereapp2.ui.guru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.learnsphereapp2.network.ApiService
import com.example.learnsphereapp2.util.PreferencesHelper

class RekapanViewModelFactory(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RekapanViewModel::class.java)) {
            return RekapanViewModel(apiService, preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
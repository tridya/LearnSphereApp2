package com.example.learnsphereapp2.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LearnSpherePrefs", Context.MODE_PRIVATE)

    fun saveUserData(
        userId: Int,
        username: String,
        nama: String,
        role: String,
        kelasId: Int? = null
    ) {
        with(sharedPreferences.edit()) {
            if (userId > 0) putInt("user_id", userId)
            putString("username", username)
            putString("nama", nama)
            putString("role", role)
            kelasId?.let { if (it > 0) putInt("kelasId", it) }
            apply()
        }
    }

    fun getUserId(): Int? {
        val userId = sharedPreferences.getInt("user_id", -1)
        return if (userId != -1) userId else null
    }

    fun saveToken(token: String) {
        if (token.isNotBlank()) {
            sharedPreferences.edit().putString("token", token).apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun saveUsername(username: String) {
        if (username.isNotBlank()) {
            sharedPreferences.edit().putString("username", username).apply()
        }
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun saveRole(role: String) {
        if (role.isNotBlank()) {
            sharedPreferences.edit().putString("role", role).apply()
        }
    }

    fun getRole(): String? {
        return sharedPreferences.getString("role", null)
    }

    fun saveNama(nama: String) {
        if (nama.isNotBlank()) {
            sharedPreferences.edit().putString("nama", nama).apply()
        }
    }

    fun getNama(): String? {
        return sharedPreferences.getString("nama", null)
    }

    fun saveKelasId(kelasId: Int) {
        if (kelasId > 0) {
            sharedPreferences.edit().putInt("kelasId", kelasId).apply()
        }
    }

    fun getKelasId(): Int? {
        val kelasId = sharedPreferences.getInt("kelasId", -1)
        return if (kelasId != -1) kelasId else null
    }

    fun saveSiswaId(siswaId: Int) {
        if (siswaId > 0) {
            sharedPreferences.edit().putInt("siswaId", siswaId).apply()
        }
    }

    fun getSiswaId(): Int? {
        val siswaId = sharedPreferences.getInt("siswaId", -1)
        return if (siswaId != -1) siswaId else null
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun setOnboardingShown(shown: Boolean) {
        sharedPreferences.edit().putBoolean("onboarding_shown", shown).apply()
    }

    fun isOnboardingShown(): Boolean {
        return sharedPreferences.getBoolean("onboarding_shown", false)
    }
}
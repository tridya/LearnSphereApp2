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
        kelasId: Int?
    ) {
        with(sharedPreferences.edit()) {
            putInt("user_id", userId)
            putString("username", username)
            putString("nama", nama)
            putString("role", role)
            kelasId?.let { putInt("kelasId", it) }
            apply()
        }
    }

    fun getUserId(): Int? {
        return sharedPreferences.getInt("user_id", -1).takeIf { it != -1 }
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun saveRole(role: String) {
        sharedPreferences.edit().putString("role", role).apply()
    }

    fun getRole(): String? {
        return sharedPreferences.getString("role", null)
    }

    fun saveNama(nama: String) {
        sharedPreferences.edit().putString("nama", nama).apply()
    }

    fun getNama(): String? {
        return sharedPreferences.getString("nama", null)
    }

    fun saveKelasId(kelasId: Int) {
        sharedPreferences.edit().putInt("kelasId", kelasId).apply()
    }

    fun getKelasId(): Int {
        return sharedPreferences.getInt("kelasId", -1)
    }

    fun saveSiswaId(siswaId: Int) {
        sharedPreferences.edit().putInt("siswaId", siswaId).apply()
    }

    fun getSiswaId(): Int {
        return sharedPreferences.getInt("siswaId", -1)
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
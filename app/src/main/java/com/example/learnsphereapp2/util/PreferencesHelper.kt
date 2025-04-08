package com.example.learnsphereapp2.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LearnSpherePrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
        Log.d("PreferencesHelper", "Saved token: $token")
    }

    fun getToken(): String? = sharedPreferences.getString("token", null).also {
        Log.d("PreferencesHelper", "Retrieved token: $it")
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
        Log.d("PreferencesHelper", "Saved username: $username")
    }

    fun getUsername(): String? = sharedPreferences.getString("username", null).also {
        Log.d("PreferencesHelper", "Retrieved username: $it")
    }

    fun saveRole(role: String) {
        sharedPreferences.edit().putString("role", role).apply()
        Log.d("PreferencesHelper", "Saved role: $role")
    }

    fun getRole(): String? = sharedPreferences.getString("role", null).also {
        Log.d("PreferencesHelper", "Retrieved role: $it")
    }

    fun saveKelasId(kelasId: Int) {
        sharedPreferences.edit().putInt("kelasId", kelasId).apply()
        Log.d("PreferencesHelper", "Saved kelasId: $kelasId")
    }

    fun getKelasId(): Int? {
        val kelasId = sharedPreferences.getInt("kelasId", 0)
        Log.d("PreferencesHelper", "Retrieved kelasId: $kelasId")
        return if (kelasId == 0) null else kelasId
    }

    fun saveNamaKelas(namaKelas: String) {
        sharedPreferences.edit().putString("namaKelas", namaKelas).apply()
        Log.d("PreferencesHelper", "Saved namaKelas: $namaKelas")
    }

    fun getNamaKelas(): String? = sharedPreferences.getString("namaKelas", null).also {
        Log.d("PreferencesHelper", "Retrieved namaKelas: $it")
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit().putInt("userId", userId).apply()
        Log.d("PreferencesHelper", "Saved userId: $userId")
    }

    fun getUserId(): Int = sharedPreferences.getInt("userId", -1).also {
        Log.d("PreferencesHelper", "Retrieved userId: $it")
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
        Log.d("PreferencesHelper", "Cleared all preferences")
    }
}
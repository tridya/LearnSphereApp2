// app/src/main/java/com/example/learnsphereapp2/util/PreferencesHelper.kt
package com.example.learnsphereapp2.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LearnSpherePrefs", Context.MODE_PRIVATE)

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

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
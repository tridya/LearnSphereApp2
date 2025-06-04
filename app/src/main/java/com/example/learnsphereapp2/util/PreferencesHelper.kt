// app/src/main/java/com/example/learnsphereapp2/util/PreferencesHelper.kt
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

    fun saveKelasId(kelasId: Int) {
        sharedPreferences.edit().putInt("kelasId", kelasId).apply()
    }

    fun getKelasId(): Int {
        return sharedPreferences.getInt("kelasId", -1) // -1 sebagai default jika tidak ada
    }

    fun saveSiswaId(siswaId: Int) {
        sharedPreferences.edit().putInt("siswaId", siswaId).apply()
    }

    fun getSiswaId(): Int {
        return sharedPreferences.getInt("siswaId", -1) // -1 sebagai default jika tidak ada
    }
    fun saveNama(nama: String) {
        sharedPreferences.edit().putString("nama", nama).apply()
    }

    fun getNama(): String? {
        return sharedPreferences.getString("nama", null)
    }

    fun saveUserData(
        userId: String,
        username: String,
        nama: String,
        role: String,
        kelasId: Int?
    ) {
        with(sharedPreferences.edit()) {
            putString("user_id", userId)
            putString("username", username)
            putString("nama", nama)
            putString("role", role)
            kelasId?.let { putInt("kelasId", it) }
            apply()
        }
    }


    fun saveUserId(userId: Int) {
        sharedPreferences.edit().putInt("userId", userId).apply()
    }


    fun saveGuruId(guruId: Int) {
        sharedPreferences.edit().putInt("guru_id", guruId).apply()
    }

    fun getGuruId(): Int? {
        return sharedPreferences.getInt("guru_id", -1).takeIf { it !=-1}
    }


//    fun getUserId(): String? = sharedPreferences.getString("user_id", null)

    fun setOnboardingShown(shown: Boolean) {
        sharedPreferences.edit().putBoolean("onboarding_shown", shown).apply()
    }

    fun isOnboardingShown(): Boolean {
        return sharedPreferences.getBoolean("onboarding_shown", false)
    }
}
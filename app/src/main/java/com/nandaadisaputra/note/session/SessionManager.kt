package com.nandaadisaputra.note.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "token"
        private const val USERNAME_KEY = "username"
        private const val PASSWORD_KEY = "password" // Added for storing password
    }

    // Simpan token login
    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    // Simpan informasi pengguna (username dan password) saat login
    fun saveUserInfo(username: String, password: String) {
        prefs.edit().putString(USERNAME_KEY, username).apply()
        prefs.edit().putString(PASSWORD_KEY, password).apply()
    }

    // Ambil token login
    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    // Ambil informasi pengguna (username dan password)
    fun getUserInfo(): Pair<String?, String?> {
        val username = prefs.getString(USERNAME_KEY, null)
        val password = prefs.getString(PASSWORD_KEY, null) // Retrieve password
        return Pair(username, password)
    }

    // Hapus token dan informasi pengguna saat logout
    fun clearSession() {
        prefs.edit().remove(TOKEN_KEY).apply()
        prefs.edit().remove(USERNAME_KEY).apply()
        prefs.edit().remove(PASSWORD_KEY).apply() // Remove password as well
    }

    // Cek apakah user sudah login
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}

package com.example.sensazionapp.util

import android.content.Context
import android.content.SharedPreferences
import com.example.sensazionapp.domain.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SensazionAppPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER = "user_data"
    }

    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveUser(user: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        editor.putString(KEY_USER, json)
        editor.apply()
    }

    fun getUser(): User? {
        val json = sharedPreferences.getString(KEY_USER, null) ?: return null
        return try {
            Gson().fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
}
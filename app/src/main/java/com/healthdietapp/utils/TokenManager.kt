package com.healthdietapp.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(Constants.TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(Constants.TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(Constants.TOKEN_KEY).apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}

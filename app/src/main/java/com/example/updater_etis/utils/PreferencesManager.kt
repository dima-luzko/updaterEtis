package com.example.updater_etis.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager private constructor(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_VERSION = "PREF_VERSION"
        const val PREF_NETWORK_IS_ACTIVE = "PREF_NETWORK_IS_ACTIVE"

        private var ourInstance: PreferencesManager? = null
        fun getInstance(context: Context): PreferencesManager {
            if (ourInstance == null) {
                ourInstance = PreferencesManager(context)
            }
            return ourInstance!!
        }
    }

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String, defValue: String): String {
        return preferences.getString(key, defValue).toString()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }
}
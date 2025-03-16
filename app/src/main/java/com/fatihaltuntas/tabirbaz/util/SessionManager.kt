package com.fatihaltuntas.tabirbaz.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    /**
     * Onboarding ekranının gösterilip gösterilmeyeceğini kaydeder
     */
    fun setOnboardingCompleted(isCompleted: Boolean) {
        editor.putBoolean(KEY_ONBOARDING_COMPLETED, isCompleted)
        editor.apply()
    }

    /**
     * Onboarding ekranının daha önce tamamlanıp tamamlanmadığını kontrol eder
     */
    fun isOnboardingCompleted(): Boolean {
        return pref.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Kullanıcı oturum durumunu kaydeder
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    /**
     * Kullanıcının oturum açıp açmadığını kontrol eder
     */
    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Oturumu temizler (çıkış yaparken)
     */
    fun clearSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "TabirbazPrefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
} 
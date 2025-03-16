package com.fatihaltuntas.tabirbaz

import android.app.Application
import android.util.Log
import com.fatihaltuntas.tabirbaz.util.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TabirbazApplication : Application() {
    
    lateinit var sessionManager: SessionManager
        private set
    
    companion object {
        private const val TAG = "TabirbazApplication"
        lateinit var instance: TabirbazApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionManager = SessionManager(this)
        initializeFirebase()
    }

    private fun initializeFirebase() {
        try {
            // Firebase'i başlat
            if (FirebaseApp.getApps(this).isEmpty()) {
                val app = FirebaseApp.initializeApp(this)
                Log.d(TAG, "Firebase initialized successfully: ${app?.name}")
            } else {
                Log.d(TAG, "Firebase was already initialized")
            }

            // Firebase servislerini başlat
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            
            // Kullanıcı zaten giriş yapmışsa oturumu kaydet
            auth.currentUser?.let {
                sessionManager.setLoggedIn(true)
            }
            
            Log.d(TAG, "Firebase services initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed", e)
            // Kritik bir hata olduğu için uygulama durumunu logluyoruz
            e.printStackTrace()
        }
    }
}


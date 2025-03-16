package com.fatihaltuntas.tabirbaz.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fatihaltuntas.tabirbaz.TabirbazApplication
import com.fatihaltuntas.tabirbaz.databinding.ActivityMainBinding
import com.fatihaltuntas.tabirbaz.util.SessionManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // SessionManager ve Firebase Auth'u başlat
        sessionManager = (application as TabirbazApplication).sessionManager
        auth = FirebaseAuth.getInstance()
        
        // Oturum durumunu kontrol et
        checkUserSession()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupWindowInsets()
    }
    
    private fun checkUserSession() {
        // Kullanıcı oturum açmamışsa, giriş ekranına yönlendir
        if (auth.currentUser == null || !sessionManager.isLoggedIn()) {
            navigateToWelcomeActivity()
            return
        }
    }
    
    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish() // Bu aktiviteyi kapat
    }
    
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
} 
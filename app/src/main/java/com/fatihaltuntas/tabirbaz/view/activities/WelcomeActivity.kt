package com.fatihaltuntas.tabirbaz.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.TabirbazApplication
import com.fatihaltuntas.tabirbaz.databinding.ActivityWelcomeBinding
import com.fatihaltuntas.tabirbaz.util.SessionManager
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SessionManager ve Firebase Auth'u başlat
        sessionManager = (application as TabirbazApplication).sessionManager
        auth = FirebaseAuth.getInstance()
        
        // Kullanıcının durumunu kontrol et
        checkUserSession()
        
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }
    
    private fun checkUserSession() {
        // Eğer kullanıcı giriş yapmışsa, doğrudan MainActivity'ye yönlendir
        if (auth.currentUser != null && sessionManager.isLoggedIn()) {
            navigateToMainActivity()
            return
        }
        
        // Onboarding ekranı daha önce gösterildiyse doğrudan giriş ekranına yönlendir
        if (sessionManager.isOnboardingCompleted()) {
            // Bu kısım daha sonra uygulanacak - navController ile Login'e yönlendireceğiz
            // Şimdilik fragment'lara erişim için activity devam etmeli
        }
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Bu aktiviteyi kapat
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        
        // Onboarding tamamlandıysa ama oturum açılmadıysa, doğrudan giriş ekranına yönlendir
        if (sessionManager.isOnboardingCompleted() && !sessionManager.isLoggedIn()) {
            navController.navigate(R.id.loginFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
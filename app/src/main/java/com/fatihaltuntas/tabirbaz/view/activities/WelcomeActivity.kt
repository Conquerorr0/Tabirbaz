package com.fatihaltuntas.tabirbaz.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fatihaltuntas.tabirbaz.databinding.ActivityWelcomeBinding
import com.fatihaltuntas.tabirbaz.view.fragments.OnboardingFragment

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupWindowInsets()
        setupClickListeners()
    }
    
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    private fun setupClickListeners() {
        binding.btnGetStarted.setOnClickListener {
            // TODO: Ana ekrana geçiş yapılacak
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        binding.btnCreateAccount.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(binding.main.id, OnboardingFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
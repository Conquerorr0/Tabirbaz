package com.fatihaltuntas.tabirbaz.view.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fatihaltuntas.tabirbaz.databinding.ActivityWelcomeBinding
import com.fatihaltuntas.tabirbaz.view.fragments.OnboardingFragment
import com.fatihaltuntas.tabirbaz.view.fragments.auth.LoginFragment
import com.fatihaltuntas.tabirbaz.view.fragments.auth.RegisterFragment
import com.fatihaltuntas.tabirbaz.viewmodel.WelcomeViewModel

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val viewModel: WelcomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupWindowInsets()
        setupClickListeners()
    }
    
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    private fun setupClickListeners() {
        binding.btnGetStarted.setOnClickListener {
            navigateToOnboarding()
        }
        
        binding.btnCreateAccount.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun navigateToOnboarding() {
        binding.welcomeContent.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, OnboardingFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToRegister() {
        binding.welcomeContent.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }
    
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            binding.welcomeContent.visibility = View.VISIBLE
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
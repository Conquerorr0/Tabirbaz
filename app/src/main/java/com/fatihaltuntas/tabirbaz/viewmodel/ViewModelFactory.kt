package com.fatihaltuntas.tabirbaz.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fatihaltuntas.tabirbaz.TabirbazApplication
import com.fatihaltuntas.tabirbaz.repository.DreamRepository
import com.fatihaltuntas.tabirbaz.repository.UserRepository
import com.fatihaltuntas.tabirbaz.viewmodel.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModelFactory sınıfı, ViewModel'leri repository'ler ile birlikte oluşturmak için kullanılır.
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val sessionManager by lazy { (application as TabirbazApplication).sessionManager }
    
    // Repository'leri lazy olarak oluştur
    private val userRepository by lazy { UserRepository(firestore, auth) }
    private val dreamRepository by lazy { DreamRepository(firestore, auth) }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository, sessionManager) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(dreamRepository) as T
            }
            modelClass.isAssignableFrom(DreamDetailViewModel::class.java) -> {
                DreamDetailViewModel(dreamRepository) as T
            }
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> {
                ExploreViewModel(dreamRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository, dreamRepository, sessionManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 
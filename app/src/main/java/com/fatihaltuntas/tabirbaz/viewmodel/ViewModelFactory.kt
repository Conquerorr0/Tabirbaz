package com.fatihaltuntas.tabirbaz.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fatihaltuntas.tabirbaz.repository.DreamRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewModelFactory(private val application: Application? = null) : ViewModelProvider.Factory {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dreamRepository = DreamRepository(firestore, auth)
        
        return when {
            modelClass.isAssignableFrom(DreamViewModel::class.java) -> {
                DreamViewModel(dreamRepository) as T
            }
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> {
                ExploreViewModel(dreamRepository) as T
            }
            modelClass.isAssignableFrom(DreamDetailViewModel::class.java) -> {
                DreamDetailViewModel(dreamRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(dreamRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                application?.let {
                    AuthViewModel(it) as T
                } ?: throw IllegalArgumentException("AuthViewModel için Application parametresi gereklidir")
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 
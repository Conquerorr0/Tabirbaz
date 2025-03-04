package com.fatihaltuntas.tabirbaz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    init {
        _currentUser.value = auth.currentUser
    }
    
    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                _currentUser.value = result.user
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message
            }
    }
    
    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                _currentUser.value = result.user
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message
            }
    }
    
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }
    
    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _error.value = "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi."
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message
            }
    }
} 
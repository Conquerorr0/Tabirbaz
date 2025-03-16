package com.fatihaltuntas.tabirbaz.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.fatihaltuntas.tabirbaz.model.UserProfile
import com.fatihaltuntas.tabirbaz.TabirbazApplication

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    
    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _profileUpdateSuccess = MutableLiveData<Boolean>()
    val profileUpdateSuccess: LiveData<Boolean> = _profileUpdateSuccess

    init {
        try {
            _currentUser.value = auth.currentUser
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = auth.signInWithCredential(credential).await()
                _currentUser.value = result.user
                result.user?.let { createUserProfile(it) }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun createUserProfile(user: FirebaseUser) {
        try {
            val userProfile = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to user.displayName,
                "photoUrl" to user.photoUrl?.toString(),
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("users").document(user.uid)
                .set(userProfile, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    sendEmailVerification(user)
                    createUserProfile(user)
                }
                _currentUser.value = result.user
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun sendEmailVerification(user: FirebaseUser) {
        try {
            _loading.value = true
            user.sendEmailVerification().await()
        } catch (e: Exception) {
            _error.value = e.message ?: "E-posta doğrulama gönderilirken bir hata oluştu"
        } finally {
            _loading.value = false
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            try {
                _loading.value = true
                auth.currentUser?.let { user ->
                    sendEmailVerification(user)
                } ?: run {
                    _error.value = "Kullanıcı oturumu bulunamadı"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "E-posta doğrulama gönderilirken bir hata oluştu"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val user = auth.currentUser
                if (user != null) {
                    db.collection("users")
                        .document(user.uid)
                        .set(userProfile)
                        .await()
                    _profileUpdateSuccess.value = true
                } else {
                    _error.value = "Kullanıcı bulunamadı"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Profil güncellenirken bir hata oluştu"
                _profileUpdateSuccess.value = false
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                auth.sendPasswordResetEmail(email).await()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
} 
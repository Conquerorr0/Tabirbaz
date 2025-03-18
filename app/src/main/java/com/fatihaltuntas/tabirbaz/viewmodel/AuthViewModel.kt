package com.fatihaltuntas.tabirbaz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import com.fatihaltuntas.tabirbaz.model.UserProfile
import com.fatihaltuntas.tabirbaz.repository.UserRepository
import com.fatihaltuntas.tabirbaz.util.SessionManager

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
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
            _currentUser.value = userRepository.getCurrentUser()
            // Kullanıcı varsa oturum bilgisini güncelle
            userRepository.getCurrentUser()?.let {
                sessionManager.setLoggedIn(true)
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val user = userRepository.signInWithEmailAndPassword(email, password)
                _currentUser.value = user
                // Başarılı giriş durumunda oturum bilgisini güncelle
                sessionManager.setLoggedIn(true)
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
                val user = userRepository.signInWithGoogle(account)
                _currentUser.value = user
                
                // Kullanıcı profili oluştur veya güncelle
                user?.let { 
                    userRepository.createUserProfile(it)
                    // Başarılı giriş durumunda oturum bilgisini güncelle
                    sessionManager.setLoggedIn(true)
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val user = userRepository.signUpWithEmailAndPassword(email, password)
                
                user?.let { newUser ->
                    // E-posta doğrulama gönder
                    userRepository.sendEmailVerification(newUser)
                    // Kullanıcı profili oluştur
                    userRepository.createUserProfile(newUser)
                    // Kayıt durumunda oturum bilgisini güncelle
                    sessionManager.setLoggedIn(true)
                }
                
                _currentUser.value = user
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            try {
                _loading.value = true
                userRepository.getCurrentUser()?.let { user ->
                    userRepository.sendEmailVerification(user)
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
                userRepository.updateUserProfile(userProfile)
                _profileUpdateSuccess.value = true
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
                userRepository.resetPassword(email)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun signOut() {
        userRepository.signOut()
        _currentUser.value = null
        // Çıkış durumunda oturum bilgisini temizle
        sessionManager.clearSession()
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
} 
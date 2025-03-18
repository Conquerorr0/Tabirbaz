package com.fatihaltuntas.tabirbaz.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihaltuntas.tabirbaz.model.Dream
import com.fatihaltuntas.tabirbaz.model.UserProfile
import com.fatihaltuntas.tabirbaz.repository.DreamRepository
import com.fatihaltuntas.tabirbaz.repository.UserRepository
import com.fatihaltuntas.tabirbaz.util.SessionManager
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val dreamRepository: DreamRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile
    
    private val _userDreams = MutableLiveData<List<Dream>>()
    val userDreams: LiveData<List<Dream>> = _userDreams
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess
    
    init {
        loadUserProfile()
        loadUserDreams()
    }
    
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val profile = userRepository.getUserProfile()
                _userProfile.value = profile
            } catch (e: Exception) {
                _error.value = e.message ?: "Profil bilgileri yüklenirken bir hata oluştu"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun loadUserDreams() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val dreams = dreamRepository.getUserDreams()
                _userDreams.value = dreams
            } catch (e: Exception) {
                _error.value = e.message ?: "Rüyalar yüklenirken bir hata oluştu"
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
                _updateSuccess.value = true
                loadUserProfile() // Profil bilgilerini yeniden yükle
            } catch (e: Exception) {
                _error.value = e.message ?: "Profil güncellenirken bir hata oluştu"
                _updateSuccess.value = false
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun signOut() {
        userRepository.signOut()
        sessionManager.clearSession()
    }
    
    fun clearError() {
        _error.value = null
    }
} 
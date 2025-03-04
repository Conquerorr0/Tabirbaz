package com.fatihaltuntas.tabirbaz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {
    private val _isFirstTime = MutableStateFlow(true)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime

    fun setFirstTimeDone() {
        viewModelScope.launch {
            _isFirstTime.value = false
        }
    }
} 
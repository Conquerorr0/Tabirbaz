package com.fatihaltuntas.tabirbaz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {
    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    private val _isLastPage = MutableLiveData<Boolean>()
    val isLastPage: LiveData<Boolean> = _isLastPage

    init {
        _currentPage.value = 0
        _isLastPage.value = false
    }

    fun setCurrentPage(position: Int, totalPages: Int) {
        _currentPage.value = position
        _isLastPage.value = position == totalPages - 1
    }
} 
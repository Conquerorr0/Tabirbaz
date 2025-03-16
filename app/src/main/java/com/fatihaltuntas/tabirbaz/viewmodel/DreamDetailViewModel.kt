package com.fatihaltuntas.tabirbaz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fatihaltuntas.tabirbaz.model.Dream
import com.fatihaltuntas.tabirbaz.repository.DreamRepository
import kotlinx.coroutines.launch

class DreamDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val dreamRepository = DreamRepository()
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _dream = MutableLiveData<Dream?>()
    val dream: LiveData<Dream?> = _dream
    
    private val _similarDreams = MutableLiveData<List<Dream>>()
    val similarDreams: LiveData<List<Dream>> = _similarDreams
    
    // Rüya detayını yükle
    fun loadDreamDetails(dreamId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                
                // Rüya detayını getir
                val dreamDetail = dreamRepository.getDreamById(dreamId)
                _dream.value = dreamDetail
                
                // Görüntülenme sayısını artır
                dreamDetail?.let {
                    dreamRepository.incrementViewCount(dreamId)
                    
                    // Benzer rüyaları getir (aynı kategorideki rüyalar)
                    loadSimilarDreams(it.categoryId, dreamId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    
    // Benzer rüyaları yükle
    private suspend fun loadSimilarDreams(categoryId: String, excludeDreamId: String) {
        try {
            val allCategoryDreams = dreamRepository.getDreamsByCategory(categoryId)
            // Mevcut rüyayı listeden çıkar
            val filteredDreams = allCategoryDreams.filter { it.id != excludeDreamId }
            // Maksimum 5 benzer rüya göster
            _similarDreams.value = filteredDreams.take(5)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 
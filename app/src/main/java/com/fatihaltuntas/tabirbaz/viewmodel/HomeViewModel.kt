package com.fatihaltuntas.tabirbaz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihaltuntas.tabirbaz.model.Dream
import com.fatihaltuntas.tabirbaz.model.DreamCategory
import com.fatihaltuntas.tabirbaz.repository.DreamRepository
import com.fatihaltuntas.tabirbaz.repository.CategoryRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val dreamRepository: DreamRepository) : ViewModel() {
    
    private val categoryRepository = CategoryRepository()
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _categories = MutableLiveData<List<DreamCategory>>()
    val categories: LiveData<List<DreamCategory>> = _categories
    
    private val _featuredDream = MutableLiveData<Dream?>()
    val featuredDream: LiveData<Dream?> = _featuredDream
    
    private val _recentDreams = MutableLiveData<List<Dream>>()
    val recentDreams: LiveData<List<Dream>> = _recentDreams
    
    private val _popularDreams = MutableLiveData<List<Dream>>()
    val popularDreams: LiveData<List<Dream>> = _popularDreams
    
    // Ana sayfa verilerini yükle
    fun loadHomeData() {
        loadCategories()
        loadFeaturedDream()
        loadRecentDreams()
        loadPopularDreams()
    }
    
    // Kategorileri yükle
    fun loadCategories() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val categoriesList = categoryRepository.getAllCategories()
                _categories.value = categoriesList
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    
    // Günün rüya yorumunu yükle
    fun loadFeaturedDream() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val dream = dreamRepository.getFeaturedDream()
                _featuredDream.value = dream
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    
    // Kullanıcının son rüyalarını yükle
    fun loadRecentDreams() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val dreams = dreamRepository.getUserDreams(limit = 5)
                _recentDreams.value = dreams
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    
    // Popüler rüyaları yükle
    fun loadPopularDreams() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val dreams = dreamRepository.getPopularDreams(limit = 5)
                _popularDreams.value = dreams
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    
    // Kategori bazlı rüyaları getir
    fun getDreamsByCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val dreams = dreamRepository.getDreamsByCategory(categoryId)
                // Bu veriler başka bir fragment'a aktarılacağı için burada LiveData olarak tutulmadı
                // CategoryDreamsFragment'a kategoriId geçirilecek ve orada çağrılacak
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 
package com.fatihaltuntas.tabirbaz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihaltuntas.tabirbaz.model.Dream
import com.fatihaltuntas.tabirbaz.model.DreamCategory
import com.fatihaltuntas.tabirbaz.repository.CategoryRepository
import com.fatihaltuntas.tabirbaz.repository.DreamRepository
import kotlinx.coroutines.launch

class ExploreViewModel(private val dreamRepository: DreamRepository) : ViewModel() {
    
    private val categoryRepository = CategoryRepository()
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _categories = MutableLiveData<List<DreamCategory>>()
    val categories: LiveData<List<DreamCategory>> = _categories
    
    private val _popularDreams = MutableLiveData<List<Dream>>()
    val popularDreams: LiveData<List<Dream>> = _popularDreams
    
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
    
    // Popüler rüyaları yükle
    fun loadPopularDreams() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val dreams = dreamRepository.getPopularDreams(limit = 10)
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
    
    // Arama yap
    fun searchDreams(query: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                // Arama işlevi ileride eklenecek
                // Şimdilik boş liste dönüyoruz
                _popularDreams.value = emptyList()
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
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
import kotlinx.coroutines.Dispatchers
import com.fatihaltuntas.tabirbaz.util.Resource

class HomeViewModel(private val dreamRepository: DreamRepository) : ViewModel() {
    
    private val categoryRepository = CategoryRepository()
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _categories = MutableLiveData<List<DreamCategory>>()
    val categories: LiveData<List<DreamCategory>> = _categories
    
    private val _featuredDream = MutableLiveData<Resource<Dream>>()
    val featuredDream: LiveData<Resource<Dream>> = _featuredDream
    
    private val _recentDreams = MutableLiveData<Resource<List<Dream>>>()
    val recentDreams: LiveData<Resource<List<Dream>>> = _recentDreams
    
    private val _popularDreams = MutableLiveData<List<Dream>>()
    val popularDreams: LiveData<List<Dream>> = _popularDreams
    
    init {
        loadCategories()
        loadFeaturedDream()
        loadRecentDreams()
    }
    
    // Ana sayfa verilerini yükle
    fun loadHomeData() {
        loadCategories()
        loadFeaturedDream()
        loadRecentDreams()
        loadPopularDreams()
    }
    
    // Kategorileri yükle
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val categoryList = dreamRepository.getCategoriesWithIds()
                _categories.value = categoryList
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    
    // Günün rüya yorumunu yükle
    private fun loadFeaturedDream() {
        _featuredDream.value = Resource.Loading()
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val featuredDreams = dreamRepository.getFeaturedDreams()
                if (featuredDreams.isNotEmpty()) {
                    _featuredDream.postValue(Resource.Success(featuredDreams.first()))
                } else {
                    _featuredDream.postValue(Resource.Error("Öne çıkan rüya bulunamadı"))
                }
            } catch (e: Exception) {
                _featuredDream.postValue(Resource.Error(e.message ?: "Bilinmeyen bir hata oluştu"))
            }
        }
    }
    
    // Kullanıcının son rüyalarını yükle
    private fun loadRecentDreams() {
        _recentDreams.value = Resource.Loading()
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = dreamRepository.getCurrentUserId()
                if (userId != null) {
                    val userDreams = dreamRepository.getUserDreams(userId)
                    // Son 5 rüyayı al
                    val recentUserDreams = userDreams.sortedByDescending { it.createdAt }.take(5)
                    _recentDreams.postValue(Resource.Success(recentUserDreams))
                } else {
                    _recentDreams.postValue(Resource.Error("Kullanıcı giriş yapmamış"))
                }
            } catch (e: Exception) {
                _recentDreams.postValue(Resource.Error(e.message ?: "Bilinmeyen bir hata oluştu"))
            }
        }
    }
    
    // Popüler rüyaları yükle
    private fun loadPopularDreams() {
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
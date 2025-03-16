package com.fatihaltuntas.tabirbaz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatihaltuntas.tabirbaz.model.Dream
import com.fatihaltuntas.tabirbaz.repository.DreamRepository
import com.fatihaltuntas.tabirbaz.util.Resource
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class DreamViewModel(private val repository: DreamRepository) : ViewModel() {

    private val _addDreamStatus = MutableLiveData<Resource<Dream>>()
    val addDreamStatus: LiveData<Resource<Dream>> = _addDreamStatus

    private val _categories = MutableLiveData<Resource<List<String>>>()
    val categories: LiveData<Resource<List<String>>> = _categories

    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _dreamDetails = MutableLiveData<Resource<Dream>>()
    val dreamDetails: LiveData<Resource<Dream>> = _dreamDetails

    init {
        loadCategories()
    }

    fun addDream(title: String, content: String) {
        _addDreamStatus.value = Resource.Loading()
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dream = Dream(
                    id = "",
                    userId = repository.getCurrentUserId() ?: "",
                    title = title,
                    content = content,
                    categoryId = _selectedCategory.value ?: "",
                    categoryName = _selectedCategory.value ?: "",
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
                
                val result = repository.addDream(dream)
                _addDreamStatus.postValue(Resource.Success(result))
            } catch (e: Exception) {
                _addDreamStatus.postValue(Resource.Error(e.message ?: "Bilinmeyen bir hata oluştu"))
            }
        }
    }

    fun getDreamById(dreamId: String) {
        _dreamDetails.value = Resource.Loading()
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dream = repository.getDreamById(dreamId)
                _dreamDetails.postValue(Resource.Success(dream))
            } catch (e: Exception) {
                _dreamDetails.postValue(Resource.Error(e.message ?: "Rüya detayları yüklenirken bir hata oluştu"))
            }
        }
    }

    private fun loadCategories() {
        _categories.value = Resource.Loading()
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categoryList = repository.getCategories()
                _categories.postValue(Resource.Success(categoryList))
            } catch (e: Exception) {
                _categories.postValue(Resource.Error(e.message ?: "Kategoriler yüklenirken bir hata oluştu"))
            }
        }
    }

    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun resetAddDreamStatus() {
        _addDreamStatus.value = Resource.Idle()
    }
} 
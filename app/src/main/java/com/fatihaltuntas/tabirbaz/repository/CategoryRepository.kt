package com.fatihaltuntas.tabirbaz.repository

import com.fatihaltuntas.tabirbaz.model.DreamCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val categoriesCollection = db.collection("categories")
    
    // Tüm kategorileri getir
    suspend fun getAllCategories(): List<DreamCategory> {
        val snapshot = categoriesCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        return snapshot.documents.mapNotNull { it.toObject(DreamCategory::class.java) }
    }
    
    // Belirli bir kategoriyi getir
    suspend fun getCategoryById(categoryId: String): DreamCategory? {
        val documentSnapshot = categoriesCollection.document(categoryId).get().await()
        return documentSnapshot.toObject(DreamCategory::class.java)
    }
    
    // Yeni kategori ekle (admin işlemleri için)
    suspend fun addCategory(category: DreamCategory): String {
        val categoryData = category.toMap()
        val documentRef = categoriesCollection.document()
        documentRef.set(categoryData).await()
        return documentRef.id
    }
    
    // Kategori güncelle (admin işlemleri için)
    suspend fun updateCategory(categoryId: String, category: DreamCategory) {
        val categoryData = category.copy(updatedAt = com.google.firebase.Timestamp.now()).toMap()
        categoriesCollection.document(categoryId).update(categoryData).await()
    }
    
    // Kategori sil (admin işlemleri için)
    suspend fun deleteCategory(categoryId: String) {
        categoriesCollection.document(categoryId).delete().await()
    }
} 
package com.fatihaltuntas.tabirbaz.repository

import com.fatihaltuntas.tabirbaz.model.Dream
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

class DreamRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dreamsCollection = db.collection("dreams")
    private val categoriesCollection = db.collection("categories")
    
    // Kullanıcının kendi rüyalarını getir
    suspend fun getUserDreams(limit: Int = 10): List<Dream> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val snapshot = dreamsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            
        return snapshot.documents.mapNotNull { it.toObject(Dream::class.java) }
    }
    
    // Öne çıkan/günün rüya yorumu
    suspend fun getFeaturedDream(): Dream? {
        val snapshot = dreamsCollection
            .whereEqualTo("isFeatured", true)
            .whereEqualTo("isPublic", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
            
        return if (snapshot.documents.isNotEmpty()) {
            snapshot.documents[0].toObject(Dream::class.java)
        } else {
            null
        }
    }
    
    // Popüler rüya yorumları
    suspend fun getPopularDreams(limit: Int = 10): List<Dream> {
        val snapshot = dreamsCollection
            .whereEqualTo("isPublic", true)
            .orderBy("viewCount", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            
        return snapshot.documents.mapNotNull { it.toObject(Dream::class.java) }
    }
    
    // Kategori bazlı rüyalar
    suspend fun getDreamsByCategory(categoryId: String, limit: Int = 10): List<Dream> {
        val snapshot = dreamsCollection
            .whereEqualTo("categoryId", categoryId)
            .whereEqualTo("isPublic", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            
        return snapshot.documents.mapNotNull { it.toObject(Dream::class.java) }
    }
    
    // Rüya detayı
    suspend fun getDreamById(dreamId: String): Dream? {
        val documentSnapshot = dreamsCollection.document(dreamId).get().await()
        return documentSnapshot.toObject(Dream::class.java)
    }
    
    // Rüya görüntülenme sayısını artır
    suspend fun incrementViewCount(dreamId: String) {
        dreamsCollection.document(dreamId)
            .update("viewCount", FieldValue.increment(1))
            .await()
    }
    
    // Yeni rüya ekle
    suspend fun addDream(dream: Dream): String {
        val dreamData = dream.toMap()
        val documentRef = dreamsCollection.document()
        documentRef.set(dreamData).await()
        return documentRef.id
    }
    
    // Rüya güncelle
    suspend fun updateDream(dreamId: String, dream: Dream) {
        val dreamData = dream.copy(updatedAt = com.google.firebase.Timestamp.now()).toMap()
        dreamsCollection.document(dreamId).update(dreamData).await()
    }
    
    // Rüya sil
    suspend fun deleteDream(dreamId: String) {
        dreamsCollection.document(dreamId).delete().await()
    }
} 
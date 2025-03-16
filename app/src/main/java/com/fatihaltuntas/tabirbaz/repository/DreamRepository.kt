package com.fatihaltuntas.tabirbaz.repository

import com.fatihaltuntas.tabirbaz.model.Dream
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import java.util.Date

class DreamRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val dreamsCollection = firestore.collection("dreams")
    private val categoriesCollection = firestore.collection("categories")
    
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
    suspend fun getDreamById(dreamId: String): Dream {
        val document = dreamsCollection.document(dreamId).get().await()
        if (document.exists()) {
            val dream = document.toObject(Dream::class.java) ?: throw Exception("Rüya verisi dönüştürülemedi")
            return dream
        } else {
            throw Exception("Rüya bulunamadı")
        }
    }
    
    // Rüya görüntülenme sayısını artır
    suspend fun incrementViewCount(dreamId: String) {
        dreamsCollection.document(dreamId)
            .update("viewCount", FieldValue.increment(1))
            .await()
    }
    
    // Yeni rüya ekle
    suspend fun addDream(dream: Dream): Dream {
        val dreamData = dream.toMap()
        val documentRef = dreamsCollection.document()
        documentRef.set(dreamData).await()
        
        // Firestore işlem tamamlandıktan sonra yeni ID ile birlikte Dream nesnesini döndür
        return dream.copy(id = documentRef.id)
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

    suspend fun getCategories(): List<String> {
        val querySnapshot = categoriesCollection.get().await()
        return querySnapshot.documents.mapNotNull { doc ->
            doc.getString("name")
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Timestamp'i Date'e çevirmek için yardımcı metot
    fun timestampToDate(timestamp: Timestamp): Date {
        return timestamp.toDate()
    }

    // Date'i Timestamp'e çevirmek için yardımcı metot
    fun dateToTimestamp(date: Date): Timestamp {
        return Timestamp(date)
    }
} 
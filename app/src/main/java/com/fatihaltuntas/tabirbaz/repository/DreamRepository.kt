package com.fatihaltuntas.tabirbaz.repository

import android.util.Log
import com.fatihaltuntas.tabirbaz.model.Dream
import com.fatihaltuntas.tabirbaz.model.DreamCategory
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class DreamRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val TAG = "DreamRepository"
    private val DREAMS_COLLECTION = "dreams"
    private val CATEGORIES_COLLECTION = "categories"
    private val USERS_COLLECTION = "users"
    
    /**
     * Güncel kullanıcı ID'sini döndürür
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Rüya ekler ve eklenen rüyayı geri döndürür
     */
    suspend fun addDream(dream: Dream): Dream = withContext(Dispatchers.IO) {
        try {
            val newDreamRef = firestore.collection(DREAMS_COLLECTION).document()
            val dreamWithId = dream.copy(id = newDreamRef.id)
            
            newDreamRef.set(dreamWithId).await()
            Log.d(TAG, "Rüya başarıyla eklendi: ${newDreamRef.id}")
            return@withContext dreamWithId
        } catch (e: Exception) {
            Log.e(TAG, "Rüya eklenirken hata oluştu", e)
            throw e
        }
    }
    
    /**
     * ID'ye göre rüya detaylarını getirir
     */
    suspend fun getDreamById(dreamId: String): Dream = withContext(Dispatchers.IO) {
        try {
            val dreamDoc = firestore.collection(DREAMS_COLLECTION).document(dreamId).get().await()
            
            if (dreamDoc.exists()) {
                val dream = dreamDoc.toObject(Dream::class.java)
                Log.d(TAG, "Rüya bulundu: $dreamId")
                return@withContext dream ?: throw Exception("Rüya dönüştürülemedi")
            } else {
                Log.e(TAG, "Rüya bulunamadı: $dreamId")
                throw Exception("Rüya bulunamadı")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Rüya getirilirken hata oluştu", e)
            throw e
        }
    }
    
    /**
     * Kullanıcıya ait rüyaları getirir
     */
    suspend fun getUserDreams(userId: String? = getCurrentUserId()): List<Dream> = withContext(Dispatchers.IO) {
        try {
            if (userId == null) {
                throw Exception("Kullanıcı ID'si bulunamadı")
            }
            
            val dreamDocs = firestore.collection(DREAMS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()
                
            val dreams = dreamDocs.toObjects(Dream::class.java)
            
            Log.d(TAG, "Kullanıcı rüyaları getirildi. Toplam: ${dreams.size}")
            return@withContext dreams
        } catch (e: Exception) {
            Log.e(TAG, "Kullanıcı rüyaları getirilirken hata oluştu", e)
            throw e
        }
    }
    
    /**
     * Kategorilerin adlarını getirir
     */
    suspend fun getCategories(): List<String> = withContext(Dispatchers.IO) {
        try {
            val categoryDocs = firestore.collection(CATEGORIES_COLLECTION)
                .orderBy("order")
                .get()
                .await()
                
            val categories = categoryDocs.documents.mapNotNull { it.getString("name") }
            
            Log.d(TAG, "Kategoriler getirildi. Toplam: ${categories.size}")
            return@withContext categories
        } catch (e: Exception) {
            Log.e(TAG, "Kategoriler getirilirken hata oluştu", e)
            throw e
        }
    }
    
    /**
     * Kategorileri ID-Name çiftleri olarak getirir
     */
    suspend fun getCategoriesWithIds(): List<DreamCategory> = withContext(Dispatchers.IO) {
        try {
            val categoryDocs = firestore.collection(CATEGORIES_COLLECTION)
                .orderBy("order")
                .get()
                .await()
                
            val categories = categoryDocs.toObjects(DreamCategory::class.java)
            
            Log.d(TAG, "Kategoriler ID'lerle getirildi. Toplam: ${categories.size}")
            return@withContext categories
        } catch (e: Exception) {
            Log.e(TAG, "Kategoriler ID'lerle getirilirken hata oluştu", e)
            throw e
        }
    }
    
    /**
     * Benzer rüyaları getirir
     */
    suspend fun getSimilarDreams(categoryId: String, dreamId: String, limit: Int = 3): List<Dream> = withContext(Dispatchers.IO) {
        try {
            val dream = getDreamById(dreamId)
            
            val similarDreamDocs = firestore.collection(DREAMS_COLLECTION)
                .whereEqualTo("categoryId", categoryId)
                .whereNotEqualTo("id", dreamId)
                .limit(limit.toLong())
                .get()
                .await()
                
            val similarDreams = similarDreamDocs.toObjects(Dream::class.java)
            
            Log.d(TAG, "Benzer rüyalar getirildi. Toplam: ${similarDreams.size}")
            return@withContext similarDreams
        } catch (e: Exception) {
            Log.e(TAG, "Benzer rüyalar getirilirken hata oluştu", e)
            throw e
        }
    }
    
    /**
     * Rüyayı yorumlar
     * Not: Gerçek uygulamada API entegrasyonu burada yapılacak
     */
    suspend fun interpretDream(dreamId: String, content: String): Dream = withContext(Dispatchers.IO) {
        try {
            // Önce rüyayı al
            val dream = getDreamById(dreamId)
            
            // Gerçek uygulamada burada API çağrısı yapılacak
            // Şu an için demo amaçlı statik bir yorum döndürüyoruz
            val interpretation = generateDemoInterpretation(content)
            
            // Rüya yorumunu güncelle
            val updatedDream = dream.copy(
                interpretation = interpretation,
                updatedAt = Timestamp.now()
            )
            
            // Firestore'da güncelle
            firestore.collection(DREAMS_COLLECTION)
                .document(dreamId)
                .set(updatedDream)
                .await()
                
            Log.d(TAG, "Rüya yorumu güncellendi: $dreamId")
            return@withContext updatedDream
        } catch (e: Exception) {
            Log.e(TAG, "Rüya yorumlanırken hata oluştu", e)
            throw e
        }
    }
    
    /**
     * Basit bir rüya yorumu oluşturur
     * Not: Gerçek uygulamada bu kısım API çağrısı ile değiştirilecek
     */
    private fun generateDemoInterpretation(content: String): String {
        val keywords = listOf("uçmak", "düşmek", "koşmak", "aramak", "kaçmak", "su", "ateş", "ev", "araba")
        val baseInterpretations = mapOf(
            "uçmak" to "Uçmak genellikle özgürlük, engelleri aşma ve hayatınızda ileriye doğru hareket etme arzusunu temsil eder.",
            "düşmek" to "Düşmek, kontrolü kaybetme korkusunu veya hayatınızdaki güvensizlik duygusunu yansıtabilir.",
            "koşmak" to "Koşmak, bir şeyden kaçma isteğini veya bir hedefe ulaşma çabanızı gösterebilir.",
            "aramak" to "Bir şey aramak, hayatınızda eksik olan bir şeyi veya cevaplarını aradığınız soruları simgeler.",
            "kaçmak" to "Kaçmak, yüzleşmekten kaçındığınız duygular veya durumlar olduğunu gösterebilir.",
            "su" to "Su, duygularınızı ve bilinçaltınızı temsil eder. Suyun durumuna göre (sakin, dalgalı, bulanık) duygusal durumunuzu yansıtabilir.",
            "ateş" to "Ateş, tutku, arzu, öfke veya dönüşümü sembolize edebilir.",
            "ev" to "Ev, güvenlik, sığınak ve kişiliğinizin farklı yönlerini temsil eder.",
            "araba" to "Araba, hayattaki yönünüzü ve kontrolünüzü simgeler."
        )
        
        val contentLower = content.lowercase()
        val matchedKeywords = keywords.filter { contentLower.contains(it) }
        
        if (matchedKeywords.isEmpty()) {
            return "Bu rüya, geçmiş deneyimlerinizin ve mevcut duygusal durumunuzun bir yansıması olabilir. Rüyanızın detaylarını düşünerek, hayatınızda şu anda neler olup bittiğini anlamaya çalışın."
        }
        
        val interpretations = matchedKeywords.map { baseInterpretations[it] ?: "" }
        val interpretation = interpretations.joinToString("\n\n")
        
        return interpretation + "\n\nBu yorum, rüyanızdaki semboller ve temalar üzerine genel bir bakış sunmaktadır. Gerçek hayat durumunuza göre bu yorumu değerlendirmeniz önemlidir."
    }
    
    /**
     * Kategoriye göre rüyaları getirir
     */
    suspend fun getDreamsByCategory(categoryId: String): List<Dream> = withContext(Dispatchers.IO) {
        try {
            val dreamDocs = firestore.collection(DREAMS_COLLECTION)
                .whereEqualTo("categoryId", categoryId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                
            val dreams = dreamDocs.toObjects(Dream::class.java)
            
            Log.d(TAG, "Kategoriye göre rüyalar getirildi. Kategori: $categoryId, Toplam: ${dreams.size}")
            return@withContext dreams
        } catch (e: Exception) {
            Log.e(TAG, "Kategoriye göre rüyalar getirilirken hata oluştu", e)
            throw e
        }
    }
} 
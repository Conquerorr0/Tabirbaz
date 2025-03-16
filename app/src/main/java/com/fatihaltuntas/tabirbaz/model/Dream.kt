package com.fatihaltuntas.tabirbaz.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Dream(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val interpretation: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val viewCount: Int = 0,
    val isFeatured: Boolean = false,
    val isPublic: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "title" to title,
            "content" to content,
            "interpretation" to interpretation,
            "categoryId" to categoryId,
            "categoryName" to categoryName,
            "viewCount" to viewCount,
            "isFeatured" to isFeatured,
            "isPublic" to isPublic,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 
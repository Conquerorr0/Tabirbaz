package com.fatihaltuntas.tabirbaz.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Dream(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val interpretation: String = "",
    val featured: Boolean = false
) : Serializable {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "title" to title,
            "content" to content,
            "categoryId" to categoryId,
            "categoryName" to categoryName,
            "interpretation" to interpretation,
            "isPublic" to false,
            "isFeatured" to featured,
            "viewCount" to 0,
            "likeCount" to 0,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 
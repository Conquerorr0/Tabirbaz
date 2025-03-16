package com.fatihaltuntas.tabirbaz.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class DreamCategory(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val order: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
            "iconUrl" to iconUrl,
            "order" to order,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 
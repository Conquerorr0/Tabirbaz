package com.fatihaltuntas.tabirbaz.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserProfile(
    @DocumentId
    val uid: String = "",
    
    @PropertyName("email")
    val email: String = "",
    
    @PropertyName("displayName")
    val displayName: String = "",
    
    @PropertyName("photoUrl")
    val photoUrl: String? = null,
    
    @PropertyName("name")
    val name: String = "",
    
    @PropertyName("birthDate")
    val birthDate: Date? = null,
    
    @PropertyName("gender")
    val gender: String = "",
    
    @ServerTimestamp
    @PropertyName("createdAt")
    val createdAt: Timestamp? = null,
    
    @ServerTimestamp
    @PropertyName("updatedAt")
    val updatedAt: Timestamp? = null,
    
    @PropertyName("dreamCount")
    val dreamCount: Int = 0,
    
    @PropertyName("interpretationCount")
    val interpretationCount: Int = 0
) {
    // Boş constructor Firebase Firestore için gerekli
    constructor() : this(
        uid = "",
        email = "",
        displayName = "",
        photoUrl = null,
        name = "",
        birthDate = null,
        gender = "",
        createdAt = null,
        updatedAt = null,
        dreamCount = 0,
        interpretationCount = 0
    )
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "displayName" to displayName,
            "photoUrl" to photoUrl,
            "name" to name,
            "birthDate" to birthDate,
            "gender" to gender,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "dreamCount" to dreamCount,
            "interpretationCount" to interpretationCount
        )
    }
} 
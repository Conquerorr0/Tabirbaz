package com.fatihaltuntas.tabirbaz.model

import java.util.Date

data class UserProfile(
    val name: String,
    val birthDate: Date?,
    val gender: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 
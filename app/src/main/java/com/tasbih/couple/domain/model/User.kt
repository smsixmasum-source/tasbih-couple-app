package com.tasbih.couple.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val coupleId: String? = null,
    val partnerId: String? = null
)

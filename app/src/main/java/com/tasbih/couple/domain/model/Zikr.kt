package com.tasbih.couple.domain.model

data class Zikr(
    val id: String = "",
    val name: String = "",
    val arabicText: String = "",
    val transliteration: String = "",
    val meaning: String = "",
    val targetCount: Int = 33,
    val category: String = "general",
    val isDefault: Boolean = true,
    val lifetimeCount: Long = 0L
)

package com.healthdietapp.data.model

data class HistoryItem(
    val id: Int,
    val age: Int,
    val gender: String?,
    val height: Double,
    val weight: Double,
    val bmi: Double,
    val bmi_category: String,
    val predicted_obesity_level: String,
    val confidence_score: Double,
    val bmr: Double,
    val daily_calorie_target: Int,
    val created_at: String
)

data class HistoryResponse(
    val total: Int,
    val items: List<HistoryItem>
)

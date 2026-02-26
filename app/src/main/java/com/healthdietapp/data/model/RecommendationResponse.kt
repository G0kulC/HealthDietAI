package com.healthdietapp.data.model

data class RecommendationResponse(
    val bmi: Double,
    val bmi_category: String,
    val predicted_obesity_level: String,
    val confidence_score: Double,
    val class_probabilities: Map<String, Double>,
    val recommended_foods: List<FoodItem>,
    val dietary_notes: String,
    val bmr: Double,
    val daily_calorie_target: Int,
    val total_foods_filtered: Int
)

data class FoodItem(
    val food_name: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val sugar: Float?,
    val sodium: Float?
)

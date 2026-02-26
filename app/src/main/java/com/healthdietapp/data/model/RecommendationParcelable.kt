package com.healthdietapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendationResponseParcelable(
    val bmi: Double,
    val bmi_category: String,
    val predicted_obesity_level: String,
    val confidence_score: Double,
    val recommended_foods: List<FoodItemParcelable>,
    val dietary_notes: String,
    val bmr: Double,
    val daily_calorie_target: Int,
    val total_foods_filtered: Int
) : Parcelable

@Parcelize
data class FoodItemParcelable(
    val food_name: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val sugar: Float,
    val sodium: Float
) : Parcelable

fun RecommendationResponse.toParcelable() = RecommendationResponseParcelable(
    bmi = bmi,
    bmi_category = bmi_category,
    predicted_obesity_level = predicted_obesity_level,
    confidence_score = confidence_score,
    recommended_foods = recommended_foods.map {
        FoodItemParcelable(
            food_name = it.food_name,
            calories = it.calories,
            protein = it.protein,
            carbs = it.carbs,
            fat = it.fat,
            sugar = it.sugar ?: 0f,
            sodium = it.sodium ?: 0f
        )
    },
    dietary_notes = dietary_notes,
    bmr = bmr,
    daily_calorie_target = daily_calorie_target,
    total_foods_filtered = total_foods_filtered
)

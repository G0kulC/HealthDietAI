package com.healthdietapp.data.model

data class RecommendationRequest(
    val age: Int,
    val height: Float,
    val weight: Float,
    val gender: String,
    val family_history_with_overweight: String,
    val favc: String,
    val caec: String,
    val smoke: String,
    val scc: String,
    val calc: String,
    val mtrans: String,
    val physical_activity: Float,
    val water_intake: Float,
    val ncp: Float,
    val tue: Float,
    val faf: Float? = null,
    val ch2o: Float? = null
)

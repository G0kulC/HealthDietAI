package com.healthdietapp.data.repository

import android.util.Log
import com.healthdietapp.data.api.ApiService
import com.healthdietapp.data.model.RecommendationRequest
import com.healthdietapp.data.model.RecommendationResponse
import com.healthdietapp.utils.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getRecommendation(request: RecommendationRequest): NetworkResult<RecommendationResponse> {
        return try {
            val response = apiService.getRecommendation(request)
            Log.d("RecommendationRepo", "Response code: ${response.code()}")
            Log.d("RecommendationRepo", "Response body: ${response.body()}")
            when {
                response.isSuccessful && response.body() != null -> {
                    NetworkResult.Success(response.body()!!)
                }
                response.code() == 401 -> NetworkResult.Error("Session expired. Please login again.", 401)
                response.code() == 422 -> NetworkResult.Error("Invalid input. Please check your form fields.", 422)
                response.code() == 500 || response.code() == 503 -> NetworkResult.Error("Server error. Please try again later.", response.code())
                else -> NetworkResult.Error("Error: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RecommendationRepo", "Exception: ${e.localizedMessage}")
            NetworkResult.Error("Network error: ${e.localizedMessage}")
        }
    }
}

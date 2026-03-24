package com.healthdietapp.data.repository

import com.healthdietapp.data.api.ApiService
import com.healthdietapp.data.model.HistoryResponse
import com.healthdietapp.utils.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getHistory(skip: Int = 0, limit: Int = 20): NetworkResult<HistoryResponse> {
        return try {
            val response = apiService.getHistory(skip, limit)
            when {
                response.isSuccessful && response.body() != null -> NetworkResult.Success(response.body()!!)
                response.code() == 401 -> NetworkResult.Error("Session expired. Please login again.", 401)
                response.code() >= 500 -> NetworkResult.Error("Server error. Please try again later.", response.code())
                else -> NetworkResult.Error("Error: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.localizedMessage}")
        }
    }
}

package com.healthdietapp.data.repository

import com.healthdietapp.data.api.ApiService
import com.healthdietapp.data.model.LoginRequest
import com.healthdietapp.data.model.RegisterRequest
import com.healthdietapp.data.model.TokenResponse
import com.healthdietapp.data.model.UserResponse
import com.healthdietapp.utils.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(request: LoginRequest): NetworkResult<TokenResponse> {
        return try {
            val response = apiService.login(request)
            when {
                response.isSuccessful && response.body() != null -> {
                    NetworkResult.Success(response.body()!!)
                }
                response.code() == 401 -> NetworkResult.Error("Invalid credentials. Please try again.", 401)
                response.code() == 422 -> NetworkResult.Error("Invalid input. Please check your form fields.", 422)
                response.code() >= 500 -> NetworkResult.Error("Server error. Please try again later.", response.code())
                else -> NetworkResult.Error("Login failed: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun register(request: RegisterRequest): NetworkResult<UserResponse> {
        return try {
            val response = apiService.register(request)
            when {
                response.isSuccessful && response.body() != null -> {
                    NetworkResult.Success(response.body()!!)
                }
                response.code() == 400 -> NetworkResult.Error("Email or username already taken.", 400)
                response.code() == 422 -> NetworkResult.Error("Invalid input. Please check your form fields.", 422)
                response.code() >= 500 -> NetworkResult.Error("Server error. Please try again later.", response.code())
                else -> NetworkResult.Error("Registration failed: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.localizedMessage}")
        }
    }
}

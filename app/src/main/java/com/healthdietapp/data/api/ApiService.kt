package com.healthdietapp.data.api

import com.healthdietapp.data.model.HistoryResponse
import com.healthdietapp.data.model.LoginRequest
import com.healthdietapp.data.model.RecommendationRequest
import com.healthdietapp.data.model.RecommendationResponse
import com.healthdietapp.data.model.RegisterRequest
import com.healthdietapp.data.model.TokenResponse
import com.healthdietapp.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @GET("users/me")
    suspend fun getUser(): Response<UserResponse>

    @POST("recommendations/ml")
    suspend fun getRecommendation(@Body request: RecommendationRequest): Response<RecommendationResponse>

    @GET("recommendations/history")
    suspend fun getHistory(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): Response<HistoryResponse>
}

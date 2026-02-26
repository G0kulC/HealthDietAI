package com.healthdietapp.di

import com.healthdietapp.data.repository.AuthRepository
import com.healthdietapp.data.repository.RecommendationRepository
import com.healthdietapp.data.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideRecommendationRepository(apiService: ApiService): RecommendationRepository {
        return RecommendationRepository(apiService)
    }
}

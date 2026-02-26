package com.healthdietapp.di

import com.healthdietapp.data.api.ApiService
import com.healthdietapp.data.api.RetrofitClient
import com.healthdietapp.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(tokenManager: TokenManager): Retrofit {
        return RetrofitClient.create(tokenManager)
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

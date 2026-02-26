package com.healthdietapp.data.api

import com.healthdietapp.utils.Constants
import com.healthdietapp.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        return if (path.contains("auth/login") || path.contains("auth/register")) {
            chain.proceed(request)
        } else {
            val token = tokenManager.getToken()
            val newRequest = if (token != null) {
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                request
            }
            chain.proceed(newRequest)
        }
    }
}

object RetrofitClient {
    fun create(tokenManager: TokenManager): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

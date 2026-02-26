package com.healthdietapp.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val full_name: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String
)

data class UserResponse(
    val id: Int,
    val email: String,
    val username: String,
    val full_name: String?,
    val is_active: Boolean
)

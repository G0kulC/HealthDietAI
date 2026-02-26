package com.healthdietapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthdietapp.data.model.LoginRequest
import com.healthdietapp.data.model.RegisterRequest
import com.healthdietapp.data.model.TokenResponse
import com.healthdietapp.data.model.UserResponse
import com.healthdietapp.data.repository.AuthRepository
import com.healthdietapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<NetworkResult<TokenResponse>?>(null)
    val loginState: StateFlow<NetworkResult<TokenResponse>?> = _loginState

    private val _registerState = MutableStateFlow<NetworkResult<UserResponse>?>(null)
    val registerState: StateFlow<NetworkResult<UserResponse>?> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = NetworkResult.Loading()
            _loginState.value = authRepository.login(LoginRequest(email, password))
        }
    }

    fun register(name: String, email: String, username: String, password: String) {
        viewModelScope.launch {
            _registerState.value = NetworkResult.Loading()
            _registerState.value = authRepository.register(
                RegisterRequest(email = email, username = username, password = password, full_name = name)
            )
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun resetRegisterState() {
        _registerState.value = null
    }
}

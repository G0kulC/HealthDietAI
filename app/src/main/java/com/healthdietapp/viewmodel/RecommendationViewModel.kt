package com.healthdietapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthdietapp.data.model.RecommendationRequest
import com.healthdietapp.data.model.RecommendationResponse
import com.healthdietapp.data.repository.RecommendationRepository
import com.healthdietapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    private val _recommendationState = MutableStateFlow<NetworkResult<RecommendationResponse>?>(null)
    val recommendationState: StateFlow<NetworkResult<RecommendationResponse>?> = _recommendationState

    fun getRecommendation(request: RecommendationRequest) {
        viewModelScope.launch {
            _recommendationState.value = NetworkResult.Loading()
            _recommendationState.value = recommendationRepository.getRecommendation(request)
        }
    }

    fun resetState() {
        _recommendationState.value = null
    }
}

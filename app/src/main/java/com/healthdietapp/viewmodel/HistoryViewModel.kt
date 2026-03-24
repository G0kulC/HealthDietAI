package com.healthdietapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthdietapp.data.model.HistoryResponse
import com.healthdietapp.data.repository.HistoryRepository
import com.healthdietapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow<NetworkResult<HistoryResponse>?>(null)
    val historyState: StateFlow<NetworkResult<HistoryResponse>?> = _historyState

    fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = NetworkResult.Loading()
            _historyState.value = historyRepository.getHistory()
        }
    }
}

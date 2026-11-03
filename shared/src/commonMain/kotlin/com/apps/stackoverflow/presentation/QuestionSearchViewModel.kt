package com.apps.stackoverflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.stackoverflow.data.NetworkMonitor
import com.apps.stackoverflow.domain.contracts.StackOverflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException

class QuestionSearchViewModel(
    private val repository: StackOverflowRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        if (!isOnline.value) {
            _uiState.value = SearchUiState.NoNetwork
        }
    }

    fun updateSearchText(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    fun search() {
        val searchTerm = _query.value.trim()
        if (searchTerm.isEmpty()) return

        if (!isOnline.value) {
            _uiState.value = SearchUiState.NoNetwork
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            repository.searchQuestions(searchTerm)
                .onSuccess { questions -> _uiState.value = SearchUiState.Success(questions) }
                .onFailure { error ->
                    _uiState.value = if (error is IOException) {
                        SearchUiState.NoNetwork
                    } else {
                        SearchUiState.Error(error.message ?: "Search generic error")
                    }
                }
        }
    }

    fun retry() {
        if (_query.value.isBlank()) {
            _uiState.value = if (isOnline.value) SearchUiState.Idle else SearchUiState.NoNetwork
        } else {
            search()
        }
    }

    fun closeOfflineNotice() {
        _uiState.value = SearchUiState.Idle
    }
}
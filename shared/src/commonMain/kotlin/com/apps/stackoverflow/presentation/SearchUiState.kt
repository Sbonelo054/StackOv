package com.apps.stackoverflow.presentation

import com.apps.stackoverflow.domain.Question

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val questions: List<Question>) : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object NoNetwork : SearchUiState
}

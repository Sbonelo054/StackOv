package com.apps.stackoverflow.presentation

import com.apps.stackoverflow.domain.Answer
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.Question

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(
        val question: Question,
        val answers: List<Answer>,
        val sort: AnswerSort,
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
    data object NoNetwork : DetailUiState
}

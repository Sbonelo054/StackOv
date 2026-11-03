package com.apps.stackoverflow.presentation

import com.apps.stackoverflow.domain.Answer
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.Question

sealed interface QuestionDetailsUiState {
    data object Loading : QuestionDetailsUiState
    data class Success(
        val question: Question,
        val answers: List<Answer>,
        val sort: AnswerSort,
    ) : QuestionDetailsUiState
    data class Error(val message: String) : QuestionDetailsUiState
    data object NoNetwork : QuestionDetailsUiState
}

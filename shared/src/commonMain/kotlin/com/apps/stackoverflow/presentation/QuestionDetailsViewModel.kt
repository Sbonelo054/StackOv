package com.apps.stackoverflow.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.stackoverflow.data.NetworkMonitor
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.contracts.StackOverflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException

class QuestionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: StackOverflowRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val questionId: Long = checkNotNull(savedStateHandle["questionId"])

    private val _uiState = MutableStateFlow<QuestionDetailsUiState>(QuestionDetailsUiState.Loading)
    val uiState: StateFlow<QuestionDetailsUiState> = _uiState.asStateFlow()

    init {
        fetchAnswers(AnswerSort.ACTIVE)
    }

    fun selectAnswerOrder(sort: AnswerSort) {
        fetchAnswers(sort)
    }

    fun retry() {
        val currentSort = (_uiState.value as? QuestionDetailsUiState.Success)?.sort ?: AnswerSort.ACTIVE
        fetchAnswers(currentSort)
    }

    private fun fetchAnswers(sort: AnswerSort) {
        val question = repository.getCachedQuestion(questionId)
        if (question == null) {
            _uiState.value = QuestionDetailsUiState.Error("detail question not found")
            return
        }

        if (!networkMonitor.isOnline.value) {
            _uiState.value = QuestionDetailsUiState.NoNetwork
            return
        }

        viewModelScope.launch {
            _uiState.value = QuestionDetailsUiState.Loading
            repository.getAnswers(questionId, sort)
                .onSuccess { answers ->
                    _uiState.value = QuestionDetailsUiState.Success(question, answers, sort)
                }
                .onFailure { error ->
                    _uiState.value = if (error is IOException) {
                        QuestionDetailsUiState.NoNetwork
                    } else {
                        QuestionDetailsUiState.Error(error.message ?: "Generic error")
                    }
                }
        }
    }
}
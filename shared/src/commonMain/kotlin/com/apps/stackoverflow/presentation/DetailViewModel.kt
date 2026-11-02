package com.apps.stackoverflow.presentation

import androidx.compose.ui.input.key.Key.Companion.R
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.stackoverflow.data.remote.NetworkMonitor
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.contracts.repository.StackOverflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: StackOverflowRepository,
    private val networkMonitor: NetworkMonitor,
    //private val context: Context,
) : ViewModel() {

    private val questionId: Long = checkNotNull(savedStateHandle["questionId"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadAnswers(AnswerSort.ACTIVE)
    }

    fun onSortSelected(sort: AnswerSort) {
        loadAnswers(sort)
    }

    fun retry() {
        val currentSort = (_uiState.value as? DetailUiState.Success)?.sort ?: AnswerSort.ACTIVE
        loadAnswers(currentSort)
    }

    private fun loadAnswers(sort: AnswerSort) {
        val question = repository.getCachedQuestion(questionId)
        if (question == null) {
            _uiState.value = DetailUiState.Error("detail question not found")
            return
        }

        if (!networkMonitor.isOnline.value) {
            _uiState.value = DetailUiState.NoNetwork
            return
        }

        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getAnswers(questionId, sort)
                .onSuccess { answers ->
                    _uiState.value = DetailUiState.Success(question, answers, sort)
                }
                .onFailure { error ->
                    _uiState.value = if (error is Exception) {
                        DetailUiState.NoNetwork
                    } else {
                        DetailUiState.Error(error.message ?: "Generic error")
                    }
                }
        }
    }
}

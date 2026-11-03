package com.apps.stackoverflow.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apps.stackoverflow.domain.Question

@Composable
internal fun QuestionResultsList(
    questions: List<Question>,
    onQuestionSelected: (Long) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(questions, key = { it.id }) { question ->
            QuestionResultCard(question = question, onClick = { onQuestionSelected(question.id) })
            HorizontalDivider()
        }
    }
}
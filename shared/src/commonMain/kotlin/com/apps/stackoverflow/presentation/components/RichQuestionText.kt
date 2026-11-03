package com.apps.stackoverflow.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.apps.stackoverflow.utils.parseHtmlToAnnotatedString

@Composable
fun RichQuestionText(
    html: String,
    modifier: Modifier = Modifier
) {
    val annotatedText = remember(html) {
        parseHtmlToAnnotatedString(html)
    }

    Text(
        text = annotatedText,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium
    )
}
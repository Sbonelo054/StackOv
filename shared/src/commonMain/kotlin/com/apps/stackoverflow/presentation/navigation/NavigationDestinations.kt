package com.apps.stackoverflow.presentation.navigation

object NavigationDestinations {

    const val QUESTION_SEARCH = "question_search"

    const val QUESTION_DETAILS = "question_details"

    const val QUESTION_IDENTIFIER = "questionId"

    const val QUESTION_DETAILS_PATTERN =
        "$QUESTION_DETAILS/{$QUESTION_IDENTIFIER}"

    fun questionDetailsPath(questionId: Long): String {
        return "$QUESTION_DETAILS/$questionId"
    }
}

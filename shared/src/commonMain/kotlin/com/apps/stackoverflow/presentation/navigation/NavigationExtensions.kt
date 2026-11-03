package com.apps.stackoverflow.presentation.navigation

import androidx.navigation.NavHostController

fun NavHostController.openQuestionDetails(
    questionId: Long
) {
    navigate(
        NavigationDestinations.questionDetailsPath(questionId)
    )
}

fun NavHostController.navigateBack() {
    popBackStack()
}

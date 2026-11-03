package com.apps.stackoverflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.apps.stackoverflow.presentation.DetailRoute
import com.apps.stackoverflow.presentation.SearchRoute

@Composable
fun StackOverflowNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.QUESTION_SEARCH
    ) {

        composable(
            route = NavigationDestinations.QUESTION_SEARCH
        ) {
            SearchRoute(
                onQuestionSelected = { questionId ->
                    navController.openQuestionDetails(questionId)
                }
            )
        }

        composable(
            route = NavigationDestinations.QUESTION_DETAILS_PATTERN,
            arguments = listOf(
                navArgument(
                    NavigationDestinations.QUESTION_IDENTIFIER
                ) {
                    type = NavType.LongType
                }
            )
        ) {
            DetailRoute(
                onBackClick = navController::navigateBack
            )
        }
    }
}

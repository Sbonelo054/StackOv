package com.apps.stackoverflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.apps.stackoverflow.presentation.DetailRoute
import com.apps.stackoverflow.presentation.SearchRoute

private const val ROUTE_SEARCH = "search"
private const val ROUTE_DETAIL = "detail/{questionId}"
private const val ARG_QUESTION_ID = "questionId"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = ROUTE_SEARCH) {
                    composable(ROUTE_SEARCH) {
                        SearchRoute(
                            onQuestionSelected = { questionId ->
                                navController.navigate("detail/$questionId")
                            },
                        )
                    }
                    composable(
                        route = ROUTE_DETAIL,
                        arguments = listOf(navArgument(ARG_QUESTION_ID) { type = NavType.LongType }),
                    ) {
                        DetailRoute(onBackClick = { navController.popBackStack() })
                    }
                }
        }
    }
}

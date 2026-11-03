package com.apps.stackoverflow.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apps.stackoverflow.presentation.components.ErrorScreen
import com.apps.stackoverflow.presentation.components.LoadingScreen
import com.apps.stackoverflow.presentation.components.NoNetworkDialog
import com.apps.stackoverflow.presentation.components.QuestionResultsList
import com.apps.stackoverflow.presentation.theme.Dimens
import com.apps.stackoverflow.presentation.theme.StackOverflowOrange
import com.apps.stackoverflow.shared.resources.Res
import com.apps.stackoverflow.shared.resources.search_logo_overflow
import com.apps.stackoverflow.shared.resources.search_logo_stack
import com.apps.stackoverflow.shared.resources.search_menu
import com.apps.stackoverflow.shared.resources.search_placeholder
import com.apps.stackoverflow.shared.resources.stack_overflow_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchRoute(
    onQuestionSelected: (Long) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        query = query,
        uiState = uiState,
        updateSearchText = viewModel::updateSearchText,
        onSearch = viewModel::search,
        onRetry = viewModel::retry,
        onDismissNoNetwork = viewModel::closeOfflineNotice,
        onQuestionSelected = onQuestionSelected,
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    query: String,
    uiState: SearchUiState,
    updateSearchText: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onDismissNoNetwork: () -> Unit,
    onQuestionSelected: (Long) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(Dimens.SpacingXS),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Menu, contentDescription = stringResource(Res.string.search_menu))
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(Res.drawable.stack_overflow_icon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(Dimens.AvatarSize),
                        )
                        Text(
                            text = stringResource(Res.string.search_logo_stack),
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(start = Dimens.SpacingXS),
                        )
                        Text(
                            text = stringResource(Res.string.search_logo_overflow),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Surface(color = StackOverflowOrange) {
                OutlinedTextField(
                    value = query,
                    onValueChange = updateSearchText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.SpacingM),
                    placeholder = { Text(stringResource(Res.string.search_placeholder)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.SearchBarCornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                )
            }

            when (uiState) {
                is SearchUiState.Idle -> {}

                is SearchUiState.Loading -> LoadingScreen()

                is SearchUiState.Error -> ErrorScreen(message = uiState.message)

                is SearchUiState.NoNetwork -> NoNetworkDialog(onDismiss = onDismissNoNetwork, onRetry = onRetry)

                is SearchUiState.Success -> QuestionResultsList(
                    questions = uiState.questions,
                    onQuestionSelected = onQuestionSelected,
                )
            }
        }
    }
}




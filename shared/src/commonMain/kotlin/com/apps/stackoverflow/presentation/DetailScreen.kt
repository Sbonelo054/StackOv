package com.apps.stackoverflow.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apps.stackoverflow.domain.Answer
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.Question
import com.apps.stackoverflow.presentation.components.ErrorScreen
import com.apps.stackoverflow.presentation.components.LoadingScreen
import com.apps.stackoverflow.presentation.components.NoNetworkDialog
import com.apps.stackoverflow.presentation.components.RichQuestionText
import com.apps.stackoverflow.presentation.theme.Dimens
import com.apps.stackoverflow.presentation.theme.StackOverflowGray
import com.apps.stackoverflow.presentation.theme.StackOverflowOrange
import com.apps.stackoverflow.shared.resources.Res
import com.apps.stackoverflow.shared.resources.detail_accepted_answer
import com.apps.stackoverflow.shared.resources.detail_answer_votes_label
import com.apps.stackoverflow.shared.resources.detail_answers_count
import com.apps.stackoverflow.shared.resources.detail_back
import com.apps.stackoverflow.shared.resources.detail_more_info
import com.apps.stackoverflow.shared.resources.detail_viewed_times
import com.apps.stackoverflow.shared.resources.sort_active
import com.apps.stackoverflow.shared.resources.sort_oldest
import com.apps.stackoverflow.shared.resources.sort_votes
import com.apps.stackoverflow.utils.UrlLauncher
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailRoute(
    onBackClick: () -> Unit,
    viewModel: QuestionDetailsViewModel = koinViewModel(),
    urlLauncher: UrlLauncher = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        selectAnswerOrder = viewModel::selectAnswerOrder,
        onRetry = viewModel::retry,
        onMoreInfoClick = {
            val link = (uiState as? QuestionDetailsUiState.Success)?.question?.link
            if (!link.isNullOrBlank()) {
                urlLauncher.openUrl(link)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    uiState: QuestionDetailsUiState,
    onBackClick: () -> Unit,
    selectAnswerOrder: (AnswerSort) -> Unit,
    onRetry: () -> Unit,
    onMoreInfoClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.detail_more_info),
                        color = StackOverflowGray,
                        modifier = Modifier.clickable(onClick = onMoreInfoClick),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.detail_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            is QuestionDetailsUiState.Loading -> LoadingScreen(modifier = Modifier.padding(innerPadding))

            is QuestionDetailsUiState.Error -> ErrorScreen(
                message = uiState.message,
                modifier = Modifier.padding(innerPadding),
            )

            is QuestionDetailsUiState.NoNetwork -> NoNetworkDialog(onDismiss = onBackClick, onRetry = onRetry)

            is QuestionDetailsUiState.Success -> DetailContent(
                question = uiState.question,
                answers = uiState.answers,
                selectedSort = uiState.sort,
                selectAnswerOrder = selectAnswerOrder,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}



@Composable
private fun DetailContent(
    question: Question,
    answers: List<Answer>,
    selectedSort: AnswerSort,
    selectAnswerOrder: (AnswerSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = Dimens.SpacingL)) {
        item {
            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimens.SpacingS),
            )
            Text(
                text = "Asked ${question.createdAtEpochSeconds}   " +
                        stringResource(Res.string.detail_viewed_times, question.viewCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.SpacingXS),
            )
            RichQuestionText(html = question.bodyHtml, modifier = Modifier.padding(top = Dimens.SpacingM))

            if (question.tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = Dimens.SpacingM),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
                ) {
                    question.tags.forEach { tag -> TagChip(tag) }
                }
            }

            OwnerRow(
                name = question.ownerName,
                avatarUrl = question.ownerAvatarUrl,
                reputation = question.ownerReputation,
                dateLabel = "Asked ${(question.createdAtEpochSeconds)}",
                modifier = Modifier.padding(top = Dimens.SpacingL, bottom = Dimens.SpacingS),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.SpacingM))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.detail_answers_count, answers.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    AnswerSort.entries.forEach { sort ->
                        TextButton(onClick = { selectAnswerOrder(sort) }) {
                            Text(
                                text = stringResource(sort.labelRes()),
                                fontWeight = if (sort == selectedSort) FontWeight.Bold else FontWeight.Normal,
                                color = if (sort == selectedSort) {
                                    StackOverflowOrange
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                }
            }
        }

        items(answers, key = { it.id }) { answer ->
            HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.SpacingM))
            AnswerItem(answer)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnswerItem(answer: Answer) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = Dimens.SpacingL),
        ) {
            Text(text = "${answer.score}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = stringResource(Res.string.detail_answer_votes_label), style = MaterialTheme.typography.labelSmall)
            if (answer.isAccepted) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(Res.string.detail_accepted_answer),
                    tint = Color(0xFF5EBA7D),
                    modifier = Modifier.padding(top = Dimens.SpacingXS),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            RichQuestionText(html = answer.bodyHtml)
            OwnerRow(
                name = answer.ownerName,
                avatarUrl = answer.ownerAvatarUrl,
                reputation = answer.ownerReputation,
                dateLabel = "Answered ${(answer.createdAtEpochSeconds)}",
                modifier = Modifier.padding(top = Dimens.SpacingM, bottom = Dimens.SpacingXS),
            )
        }
    }
}

@Composable
private fun OwnerRow(
    name: String,
    avatarUrl: String?,
    reputation: Long,
    dateLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = dateLabel, style = MaterialTheme.typography.labelSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = Dimens.SpacingXS),
        ) {
            if (avatarUrl != null) {
//                AsyncImage(
//                    model = avatarUrl,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(Dimens.AvatarSize)
//                        .clip(CircleShape),
//                )
            }
            Column(modifier = Modifier.padding(start = Dimens.SpacingS)) {
                Text(text = name, style = MaterialTheme.typography.bodySmall)
                Text(text = "$reputation", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

private fun AnswerSort.labelRes(): StringResource = when (this) {
    AnswerSort.ACTIVE -> Res.string.sort_active
    AnswerSort.OLDEST -> Res.string.sort_oldest
    AnswerSort.VOTES -> Res.string.sort_votes
}

@Composable
private fun TagChip(tag: String) {
    Text(
        text = tag,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.TagCornerRadius))
            .background(StackOverflowOrange)
            .padding(horizontal = Dimens.SpacingS, vertical = Dimens.SpacingXS),
    )
}


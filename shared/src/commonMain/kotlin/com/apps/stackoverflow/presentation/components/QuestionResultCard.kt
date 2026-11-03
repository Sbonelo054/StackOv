package com.apps.stackoverflow.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.apps.stackoverflow.domain.Question
import com.apps.stackoverflow.presentation.theme.Dimens
import com.apps.stackoverflow.presentation.theme.StackOverflowBlack
import com.apps.stackoverflow.presentation.theme.StackOverflowBlue
import com.apps.stackoverflow.presentation.theme.StackOverflowGray
import com.apps.stackoverflow.presentation.theme.StackOverflowOrange
import com.apps.stackoverflow.shared.resources.Res
import com.apps.stackoverflow.shared.resources.ic_chevron_right
import com.apps.stackoverflow.shared.resources.search_answers_count
import com.apps.stackoverflow.shared.resources.search_asked_by_prefix
import com.apps.stackoverflow.shared.resources.search_question_title_prefix
import com.apps.stackoverflow.shared.resources.search_views_count
import com.apps.stackoverflow.shared.resources.search_votes_count
import com.apps.stackoverflow.utils.formatAskedDate
import com.apps.stackoverflow.utils.stripHtml
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuestionResultCard(
    question: Question,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Dimens.SpacingM),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = StackOverflowOrange,
            modifier = Modifier
                .padding(end = Dimens.SpacingS)
                .size(Dimens.CheckIconSize)
                .align(Alignment.CenterVertically),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.search_question_title_prefix, stripHtml(question.title)),
                color = StackOverflowBlue,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stripHtml(question.bodyHtml),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                color = StackOverflowGray,
                modifier = Modifier.padding(top = Dimens.SpacingXXS),
            )
            Text(
                text = buildAnnotatedString {
                    append(stringResource(Res.string.search_asked_by_prefix, formatAskedDate(question.createdAtEpochSeconds)))
                    append(" ")
                    withStyle(SpanStyle(color = StackOverflowBlue)) {
                        append(question.ownerName)
                    }
                },
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowBlack,
                modifier = Modifier.padding(top = Dimens.SpacingS),
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = Dimens.SpacingS)
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = stringResource(Res.string.search_answers_count, question.answerCount),
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowGray,
            )
            Text(
                text = stringResource(Res.string.search_votes_count, question.score),
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowGray,
            )
            Text(
                text = stringResource(Res.string.search_views_count, question.viewCount),
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowGray,
            )
        }

        Icon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .padding(start = Dimens.SpacingXXS)
                .size(Dimens.ChevronIconSize)
                .align(Alignment.CenterVertically),
        )
    }
}

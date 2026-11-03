package com.apps.stackoverflow.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MobiledataOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.apps.stackoverflow.shared.resources.Res
import com.apps.stackoverflow.shared.resources.no_network_dismiss
import com.apps.stackoverflow.shared.resources.no_network_message
import com.apps.stackoverflow.shared.resources.no_network_retry
import com.apps.stackoverflow.shared.resources.no_network_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoNetworkDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.MobiledataOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = { Text(stringResource(Res.string.no_network_title)) },
        text = { Text(stringResource(Res.string.no_network_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(Res.string.no_network_retry))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.no_network_dismiss))
            }
        },
    )
}

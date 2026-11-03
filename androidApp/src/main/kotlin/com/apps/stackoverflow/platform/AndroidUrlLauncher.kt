package com.apps.stackoverflow.platform

import android.content.Context
import android.content.Intent
import com.apps.stackoverflow.utils.UrlLauncher
import androidx.core.net.toUri

class AndroidUrlLauncher(
    private val context: Context
) : UrlLauncher {

    override fun openUrl(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            url.toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}
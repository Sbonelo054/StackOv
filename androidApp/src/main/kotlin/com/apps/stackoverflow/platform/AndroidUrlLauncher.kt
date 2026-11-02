package com.apps.stackoverflow.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.apps.stackoverflow.utils.UrlLauncher

class AndroidUrlLauncher(
    private val context: Context
) : UrlLauncher {

    override fun openUrl(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}
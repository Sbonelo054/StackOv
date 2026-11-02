package com.apps.stackoverflow.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val months = listOf(
    "Jan", "Feb", "Mar", "Apr",
    "May", "Jun", "Jul", "Aug",
    "Sep", "Oct", "Nov", "Dec"
)

internal fun formatAskedDate(epochSeconds: Long): String {
    val date = Instant
        .fromEpochSeconds(epochSeconds)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val month = months[date.monthNumber - 1]
    val day = date.dayOfMonth
    val year = (date.year % 100).toString().padStart(2, '0')

    return "$month $day '$year"
}

internal fun stripHtml(html: String): String {
    return html
        .replace(Regex("<[^>]*>"), " ")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace(Regex("\\s+"), " ")
        .trim()
}

interface UrlLauncher {
    fun openUrl(url: String)
}
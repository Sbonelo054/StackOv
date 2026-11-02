package com.apps.stackoverflow.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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


fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
    val tokens = Regex(
        """(<[^>]+>|[^<]+)"""
    ).findAll(html).map { it.value }.toList()

    val result = AnnotatedString.Builder()
    val styles = mutableListOf<Pair<String, Int>>()
    val links = mutableListOf<Pair<String, Int>>()

    fun appendNewlines(count: Int) {
        if (result.length == 0) return
        val current = result.toAnnotatedString().text
        val existing = current.takeLastWhile { it == '\n' }.length
        repeat((count - existing).coerceAtLeast(0)) {
            result.append("\n")
        }
    }

    tokens.forEach { token ->
        if (!token.startsWith("<")) {
            result.append(decodeHtmlEntities(token))
            return@forEach
        }

        val tagName = Regex("""^</?\s*([a-zA-Z0-9]+)""")
            .find(token)
            ?.groupValues
            ?.get(1)
            ?.lowercase()
            ?: return@forEach

        val closing = token.startsWith("</")

        when (tagName) {
            "br" -> appendNewlines(1)

            "p", "div" -> {
                if (closing) appendNewlines(2)
            }

            "li" -> {
                if (!closing) {
                    appendNewlines(1)
                    result.append("• ")
                }
            }

            "h1", "h2", "h3", "h4" -> {
                if (closing) appendNewlines(2)
                else appendNewlines(1)
            }

            "b", "strong", "i", "em", "code" -> {
                if (!closing) {
                    styles.add(tagName to result.length)
                } else {
                    val index = styles.indexOfLast { it.first == tagName }
                    if (index >= 0) {
                        val start = styles.removeAt(index).second
                        val style = when (tagName) {
                            "b", "strong" -> SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                            "i", "em" -> SpanStyle(
                                fontStyle = FontStyle.Italic
                            )
                            else -> SpanStyle(
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (start < result.length) {
                            result.addStyle(style, start, result.length)
                        }
                    }
                }
            }

            "a" -> {
                if (!closing) {
                    val href = Regex(
                        """href\s*=\s*["']([^"']+)["']""",
                        RegexOption.IGNORE_CASE
                    ).find(token)?.groupValues?.get(1)

                    if (href != null) {
                        links.add(decodeHtmlEntities(href) to result.length)
                    }
                } else {
                    if (links.isNotEmpty()) {
                        val (url, start) = links.removeAt(links.lastIndex)
                        if (start < result.length) {
                            result.addLink(
                                LinkAnnotation.Url(
                                    url = url,
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = Color(0xFF0074CC)
                                        )
                                    )
                                ),
                                start = start,
                                end = result.length
                            )
                        }
                    }
                }
            }
        }
    }

    return result.toAnnotatedString()
}

private fun decodeHtmlEntities(value: String): String {
    return value
        .replace("&nbsp;", "\u00A0")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
}

interface UrlLauncher {
    fun openUrl(url: String)
}
package com.apps.stackoverflow.domain

data class Answer(
    val id: Long,
    val questionId: Long,
    val bodyHtml: String,
    val ownerName: String,
    val ownerAvatarUrl: String?,
    val ownerReputation: Long,
    val isAccepted: Boolean,
    val score: Int,
    val createdAtEpochSeconds: Long,
)

package com.apps.stackoverflow.data.remote.repository

import com.apps.stackoverflow.data.remote.QuestionsService
import com.apps.stackoverflow.data.remote.dto.AnswersResponseDto
import com.apps.stackoverflow.data.remote.dto.SearchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorQuestionsService(
    private val client: HttpClient
) : QuestionsService {

    override suspend fun searchQuestions(
        title: String,
        pageSize: Int,
        order: String,
        sort: String,
        site: String,
        filter: String,
    ): SearchResponseDto {

        return client.get(
            "${QuestionsService.BASE_URL}search/advanced"
        ) {
            parameter("title", title)
            parameter("pagesize", pageSize)
            parameter("order", order)
            parameter("sort", sort)
            parameter("site", site)
            parameter("filter", filter)
        }.body()
    }

    override suspend fun getAnswers(
        questionId: Long,
        order: String,
        sort: String,
        site: String,
        filter: String,
    ): AnswersResponseDto {

        return client.get(
            "${QuestionsService.Companion.BASE_URL}questions/$questionId/answers"
        ) {
            parameter("order", order)
            parameter("sort", sort)
            parameter("site", site)
            parameter("filter", filter)
        }.body()
    }
}
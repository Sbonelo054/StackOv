package com.apps.stackoverflow.data.remote

import com.apps.stackoverflow.data.remote.dto.AnswersResponseDto
import com.apps.stackoverflow.data.remote.dto.SearchResponseDto

interface QuestionsService {

    suspend fun searchQuestions(
        title: String,
        pageSize: Int = 20,
        order: String = "desc",
        sort: String = "activity",
        site: String = "stackoverflow",
        filter: String = "withbody",
    ): SearchResponseDto

    suspend fun getAnswers(
        questionId: Long,
        order: String = "desc",
        sort: String = "activity",
        site: String = "stackoverflow",
        filter: String = "withbody",
    ): AnswersResponseDto

    companion object {
        const val BASE_URL = "https://api.stackexchange.com/2.2/"
    }
}
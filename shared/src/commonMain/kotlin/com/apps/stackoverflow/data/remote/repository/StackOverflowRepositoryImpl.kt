package com.apps.stackoverflow.data.remote.repository

import com.apps.stackoverflow.data.remote.StackExchangeApi
import com.apps.stackoverflow.domain.Answer
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.Question
import com.apps.stackoverflow.domain.contracts.StackOverflowRepository
import com.apps.stackoverflow.domain.toDomain

class StackOverflowRepositoryImpl(
    private val api: StackExchangeApi,
) : StackOverflowRepository {

    private val questionCache = HashMap<Long, Question>()

    override suspend fun searchQuestions(query: String): Result<List<Question>> = runCatching {
        api.searchQuestions(title = query).items.map { it.toDomain() }.also { questions ->
            questions.forEach { questionCache[it.id] = it }
        }
    }

    override suspend fun getAnswers(questionId: Long, sort: AnswerSort): Result<List<Answer>> = runCatching {
        api.getAnswers(questionId, sort = sort.apiSort, order = sort.apiOrder).items.map { it.toDomain() }
    }

    override fun getCachedQuestion(questionId: Long): Question? = questionCache[questionId]
}
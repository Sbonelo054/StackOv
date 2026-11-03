package com.apps.stackoverflow.domain.contracts

import com.apps.stackoverflow.domain.Answer
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.Question

interface StackOverflowRepository {

    suspend fun searchQuestions(query: String): Result<List<Question>>

    suspend fun getAnswers(questionId: Long, sort: AnswerSort): Result<List<Answer>>

    fun getCachedQuestion(questionId: Long): Question?
}
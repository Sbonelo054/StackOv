package com.apps.stackoverflow

import com.apps.stackoverflow.data.remote.QuestionsService
import com.apps.stackoverflow.data.remote.repository.StackOverflowRepositoryImpl
import com.apps.stackoverflow.domain.AnswerSort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StackOverflowRepositoryImplTest {

    private lateinit var api: QuestionsService
    private lateinit var repository: StackOverflowRepositoryImpl

    private val questionId = 123L

    @BeforeEach
    fun setup() {
        api = mockk()
        repository = StackOverflowRepositoryImpl(api)
    }

    @Test
    fun `getCachedQuestion returns null when cache is empty`() {
        assertNull(repository.getCachedQuestion(questionId))
    }

    @Test
    fun `searchQuestions returns empty list when API has no results`() = runTest {
        coEvery {
            api.searchQuestions(title = "Kotlin")
        } returns mockk {
            every { items } returns emptyList()
        }

        val result = repository.searchQuestions("Kotlin")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())

        coVerify(exactly = 1) {
            api.searchQuestions(title = "Kotlin")
        }
    }

    @Test
    fun `searchQuestions passes query to API without modification`() = runTest {
        val query = "  Jetpack Compose  "

        coEvery {
            api.searchQuestions(title = query)
        } returns mockk {
            every { items } returns emptyList()
        }

        val result = repository.searchQuestions(query)

        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            api.searchQuestions(title = query)
        }
    }

    @Test
    fun `searchQuestions returns failure when API throws exception`() = runTest {
        val exception = IllegalStateException("Search API failed")

        coEvery {
            api.searchQuestions(title = "Android")
        } throws exception

        val result = repository.searchQuestions("Android")

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
    }

    @Test
    fun `failed search does not populate cache`() = runTest {
        coEvery {
            api.searchQuestions(title = "Android")
        } throws IllegalStateException("Network unavailable")

        val result = repository.searchQuestions("Android")

        assertTrue(result.isFailure)
        assertNull(repository.getCachedQuestion(questionId))
    }

    @Test
    fun `empty search does not populate cache`() = runTest {
        coEvery {
            api.searchQuestions(title = "Unknown")
        } returns mockk {
            every { items } returns emptyList()
        }

        repository.searchQuestions("Unknown")

        assertNull(repository.getCachedQuestion(questionId))
    }

    @Test
    fun `getAnswers returns empty list when API has no answers`() = runTest {
        coEvery {
            api.getAnswers(
                questionId = questionId,
                sort = AnswerSort.ACTIVE.apiSort,
                order = AnswerSort.ACTIVE.apiOrder
            )
        } returns mockk {
            every { items } returns emptyList()
        }

        val result = repository.getAnswers(
            questionId = questionId,
            sort = AnswerSort.ACTIVE
        )

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `getAnswers uses ACTIVE sorting parameters`() = runTest {
        verifyAnswerSort(AnswerSort.ACTIVE)
    }

    @Test
    fun `getAnswers uses OLDEST sorting parameters`() = runTest {
        verifyAnswerSort(AnswerSort.OLDEST)
    }

    @Test
    fun `getAnswers uses VOTES sorting parameters`() = runTest {
        verifyAnswerSort(AnswerSort.VOTES)
    }

    @Test
    fun `getAnswers returns failure when API throws exception`() = runTest {
        val exception = IllegalStateException("Answers API failed")

        coEvery {
            api.getAnswers(
                questionId = questionId,
                sort = AnswerSort.ACTIVE.apiSort,
                order = AnswerSort.ACTIVE.apiOrder
            )
        } throws exception

        val result = repository.getAnswers(
            questionId = questionId,
            sort = AnswerSort.ACTIVE
        )

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
    }

    private suspend fun verifyAnswerSort(sort: AnswerSort) {
        coEvery {
            api.getAnswers(
                questionId = questionId,
                sort = sort.apiSort,
                order = sort.apiOrder
            )
        } returns mockk {
            every { items } returns emptyList()
        }

        val result = repository.getAnswers(
            questionId = questionId,
            sort = sort
        )

        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            api.getAnswers(
                questionId = questionId,
                sort = sort.apiSort,
                order = sort.apiOrder
            )
        }

        assertEquals(emptyList<Any>(), result.getOrThrow())
    }
}

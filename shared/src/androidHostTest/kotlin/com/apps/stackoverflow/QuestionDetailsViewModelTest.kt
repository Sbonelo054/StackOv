package com.apps.stackoverflow

import androidx.lifecycle.SavedStateHandle
import com.apps.stackoverflow.data.NetworkMonitor
import com.apps.stackoverflow.domain.AnswerSort
import com.apps.stackoverflow.domain.contracts.StackOverflowRepository
import com.apps.stackoverflow.presentation.QuestionDetailsUiState
import com.apps.stackoverflow.presentation.QuestionDetailsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherRule::class)
class QuestionDetailsViewModelTest {

    private val questionId = 123L

    private lateinit var repository: StackOverflowRepository
    private lateinit var networkMonitor: NetworkMonitor

    @BeforeEach
    fun setup() {
        repository = mockk()
        networkMonitor = mockk()

        every { networkMonitor.isOnline } returns MutableStateFlow(true)
    }

    private fun createViewModel(): QuestionDetailsViewModel {
        return QuestionDetailsViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf("questionId" to questionId)
            ),
            repository = repository,
            networkMonitor = networkMonitor
        )
    }

    @Test
    fun `initial state is Loading when answers request is pending`() = runTest {
        val question = mockQuestion()

        every { repository.getCachedQuestion(questionId) } returns question

        coEvery {
            repository.getAnswers(questionId, AnswerSort.ACTIVE)
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        assertEquals(
            QuestionDetailsUiState.Loading,
            viewModel.uiState.value
        )
    }

    @Test
    fun `initial load returns Success when repository succeeds`() = runTest {
        val question = mockQuestion()

        every { repository.getCachedQuestion(questionId) } returns question

        coEvery {
            repository.getAnswers(questionId, AnswerSort.ACTIVE)
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = assertInstanceOf(
            QuestionDetailsUiState.Success::class.java,
            viewModel.uiState.value
        )

        assertEquals(question, state.question)
        assertEquals(emptyList<Any>(), state.answers)
        assertEquals(AnswerSort.ACTIVE, state.sort)

        coVerify(exactly = 1) {
            repository.getAnswers(questionId, AnswerSort.ACTIVE)
        }
    }

    @Test
    fun `missing cached question returns Error`() = runTest {
        every {
            repository.getCachedQuestion(questionId)
        } returns null

        val viewModel = createViewModel()

        val state = assertInstanceOf(
            QuestionDetailsUiState.Error::class.java,
            viewModel.uiState.value
        )

        assertEquals("detail question not found", state.message)

        coVerify(exactly = 0) {
            repository.getAnswers(any(), any())
        }
    }

    @Test
    fun `offline connection returns NoNetwork`() = runTest {
        val question = mockQuestion()

        every { repository.getCachedQuestion(questionId) } returns question
        every { networkMonitor.isOnline } returns MutableStateFlow(false)

        val viewModel = createViewModel()

        assertEquals(
            QuestionDetailsUiState.NoNetwork,
            viewModel.uiState.value
        )

        coVerify(exactly = 0) {
            repository.getAnswers(any(), any())
        }
    }

    @Test
    fun `selectAnswerOrder loads answers using selected sort`() = runTest {
        val question = mockQuestion()

        every { repository.getCachedQuestion(questionId) } returns question

        coEvery {
            repository.getAnswers(questionId, any())
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        advanceUntilIdle()

        viewModel.selectAnswerOrder(AnswerSort.VOTES)

        advanceUntilIdle()

        val state = assertInstanceOf(
            QuestionDetailsUiState.Success::class.java,
            viewModel.uiState.value
        )

        assertEquals(AnswerSort.VOTES, state.sort)

        coVerify(exactly = 1) {
            repository.getAnswers(questionId, AnswerSort.VOTES)
        }
    }

    @Test
    fun `retry reloads answers with current selected sort`() = runTest {
        val question = mockQuestion()

        every { repository.getCachedQuestion(questionId) } returns question

        coEvery {
            repository.getAnswers(questionId, any())
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        advanceUntilIdle()

        viewModel.selectAnswerOrder(AnswerSort.OLDEST)
        advanceUntilIdle()

        viewModel.retry()
        advanceUntilIdle()

        coVerify(exactly = 2) {
            repository.getAnswers(questionId, AnswerSort.OLDEST)
        }
    }

    @Test
    fun `IOException returns NoNetwork`() = runTest {
        val question = mockQuestion()

        every { repository.getCachedQuestion(questionId) } returns question

        coEvery {
            repository.getAnswers(questionId, AnswerSort.ACTIVE)
        } returns Result.failure(IOException("Connection lost"))

        val viewModel = createViewModel()

        advanceUntilIdle()

        assertEquals(
            QuestionDetailsUiState.NoNetwork,
            viewModel.uiState.value
        )
    }

    @Test
    fun `unexpected API failure returns Error`() = runTest {
        val question = mockQuestion()

        every { repository.getCachedQuestion(questionId) } returns question

        coEvery {
            repository.getAnswers(questionId, AnswerSort.ACTIVE)
        } returns Result.failure(IllegalStateException("Server error"))

        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = assertInstanceOf(
            QuestionDetailsUiState.Error::class.java,
            viewModel.uiState.value
        )

        assertEquals("Server error", state.message)
    }

    @Test
    fun `missing questionId throws IllegalStateException`() {
        assertThrows(IllegalStateException::class.java) {
            QuestionDetailsViewModel(
                savedStateHandle = SavedStateHandle(),
                repository = repository,
                networkMonitor = networkMonitor
            )
        }
    }

    private fun mockQuestion(): com.apps.stackoverflow.domain.Question {
        return mockk(relaxed = true)
    }
}
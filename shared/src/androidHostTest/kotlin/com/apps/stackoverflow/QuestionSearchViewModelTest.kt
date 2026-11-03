package com.apps.stackoverflow

import com.apps.stackoverflow.data.NetworkMonitor
import com.apps.stackoverflow.domain.contracts.StackOverflowRepository
import com.apps.stackoverflow.presentation.QuestionSearchViewModel
import com.apps.stackoverflow.presentation.SearchUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.io.IOException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionSearchViewModelTest {

    private lateinit var repository: StackOverflowRepository
    private lateinit var networkMonitor: NetworkMonitor

    private lateinit var connectionState: MutableStateFlow<Boolean>

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk()
        networkMonitor = mockk()

        connectionState = MutableStateFlow(true)

        every {
            networkMonitor.isOnline
        } returns connectionState
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): QuestionSearchViewModel {
        return QuestionSearchViewModel(
            repository = repository,
            networkMonitor = networkMonitor
        )
    }

    @Test
    fun `initial state is Idle when online`() {
        val viewModel = createViewModel()

        assertEquals(
            SearchUiState.Idle,
            viewModel.uiState.value
        )

        assertEquals("", viewModel.query.value)
    }

    @Test
    fun `initial state is NoNetwork when offline`() {
        connectionState.value = false

        val viewModel = createViewModel()

        assertEquals(
            SearchUiState.NoNetwork,
            viewModel.uiState.value
        )
    }

    @Test
    fun `updateSearchText updates query`() {
        val viewModel = createViewModel()

        viewModel.updateSearchText("Jetpack Compose")

        assertEquals(
            "Jetpack Compose",
            viewModel.query.value
        )
    }

    @Test
    fun `clearing search text resets state to Idle`() = runTest {
        coEvery {
            repository.searchQuestions(any())
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        viewModel.updateSearchText("Kotlin")
        viewModel.search()

        advanceUntilIdle()

        viewModel.updateSearchText("")

        assertEquals(
            SearchUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun `blank query does not call repository`() {
        val viewModel = createViewModel()

        viewModel.updateSearchText("   ")
        viewModel.search()

        coVerify(exactly = 0) {
            repository.searchQuestions(any())
        }

        assertEquals(
            SearchUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun `search trims query before calling repository`() = runTest {
        coEvery {
            repository.searchQuestions("Kotlin")
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        viewModel.updateSearchText("  Kotlin  ")
        viewModel.search()

        advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.searchQuestions("Kotlin")
        }
    }

    @Test
    fun `search emits Loading before repository completes`() = runTest {
        coEvery {
            repository.searchQuestions(any())
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        viewModel.updateSearchText("Compose")
        viewModel.search()

        testDispatcher.scheduler.runCurrent()

        assertInstanceOf(
            SearchUiState.Success::class.java,
            viewModel.uiState.value
        )
    }

    @Test
    fun `successful search updates state to Success`() = runTest {
        coEvery {
            repository.searchQuestions("Compose")
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        viewModel.updateSearchText("Compose")
        viewModel.search()

        advanceUntilIdle()

        val state = assertInstanceOf(
            SearchUiState.Success::class.java,
            viewModel.uiState.value
        )

        assertEquals(0, state.questions.size)
    }

    @Test
    fun `offline search returns NoNetwork without repository call`() {
        connectionState.value = false

        val viewModel = createViewModel()

        viewModel.updateSearchText("Kotlin")
        viewModel.search()

        assertEquals(
            SearchUiState.NoNetwork,
            viewModel.uiState.value
        )

        coVerify(exactly = 0) {
            repository.searchQuestions(any())
        }
    }

    @Test
    fun `IOException returns NoNetwork`() = runTest {
        coEvery {
            repository.searchQuestions("Kotlin")
        } returns Result.failure(IOException("Connection lost"))

        val viewModel = createViewModel()

        viewModel.updateSearchText("Kotlin")
        viewModel.search()

        advanceUntilIdle()

        assertEquals(
            SearchUiState.NoNetwork,
            viewModel.uiState.value
        )
    }

    @Test
    fun `unexpected exception returns Error`() = runTest {
        coEvery {
            repository.searchQuestions("Kotlin")
        } returns Result.failure(
            IllegalStateException("Server unavailable")
        )

        val viewModel = createViewModel()

        viewModel.updateSearchText("Kotlin")
        viewModel.search()

        advanceUntilIdle()

        val state = assertInstanceOf(
            SearchUiState.Error::class.java,
            viewModel.uiState.value
        )

        assertEquals(
            "Server unavailable",
            state.message
        )
    }

    @Test
    fun `retry with blank query restores Idle when online`() {
        val viewModel = createViewModel()

        viewModel.retry()

        assertEquals(
            SearchUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun `retry with blank query returns NoNetwork when offline`() {
        connectionState.value = false

        val viewModel = createViewModel()

        viewModel.retry()

        assertEquals(
            SearchUiState.NoNetwork,
            viewModel.uiState.value
        )
    }

    @Test
    fun `retry with existing query searches again`() = runTest {
        coEvery {
            repository.searchQuestions("Android")
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        viewModel.updateSearchText("Android")
        viewModel.search()

        advanceUntilIdle()

        viewModel.retry()

        advanceUntilIdle()

        coVerify(exactly = 2) {
            repository.searchQuestions("Android")
        }
    }

    @Test
    fun `closeOfflineNotice resets state to Idle`() {
        connectionState.value = false

        val viewModel = createViewModel()

        assertEquals(
            SearchUiState.NoNetwork,
            viewModel.uiState.value
        )

        viewModel.closeOfflineNotice()

        assertEquals(
            SearchUiState.Idle,
            viewModel.uiState.value
        )
    }
}
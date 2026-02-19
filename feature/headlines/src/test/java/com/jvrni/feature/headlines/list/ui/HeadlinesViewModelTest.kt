package com.jvrni.feature.headlines.list.ui

import android.content.Context
import app.cash.turbine.test
import coil.ImageLoader
import com.jvrni.core.common.result.AppResult
import com.jvrni.core.navigation.HeadlineRoute
import com.jvrni.domain.models.Headline
import com.jvrni.domain.models.Source
import com.jvrni.domain.usecase.GetHeadlines
import com.jvrni.feature.headlines.list.contract.HeadlinesContract
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HeadlinesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getHeadlines: GetHeadlines
    private lateinit var imageLoader: ImageLoader
    private lateinit var context: Context
    private lateinit var viewModel: HeadlinesViewModel

    private val title = "BBC News"

    private val mockHeadlines = listOf(
        Headline(
            source = Source(id = "bbc-news", name = "BBC News", category = null),
            author = "Author One",
            title = "Kotlin is great",
            description = "Description one",
            url = "https://example.com/1",
            urlToImage = "https://example.com/image1.jpg",
            publishedAt = "01 Jan, 12:00 PM",
            content = "Content one"
        ),
        Headline(
            source = Source(id = "cnn", name = "CNN", category = null),
            author = "Author Two",
            title = "Jetpack Compose tutorial",
            description = "Description two",
            url = "https://example.com/2",
            urlToImage = "https://example.com/image2.jpg",
            publishedAt = "02 Jan, 01:00 PM",
            content = "Content two"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getHeadlines = mockk()
        imageLoader = mockk(relaxed = true)
        context = mockk()
        every { context.applicationContext } returns context

        viewModel = HeadlinesViewModel(getHeadlines, imageLoader, context, title)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region State: initial

    @Test
    fun `initial state has correct title and defaults`() = runTest(testDispatcher) {
        val state = viewModel.state.value

        assertEquals(title, state.title)
        assertEquals("", state.searchQuery)
        assertTrue(state.headlines.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isError)
    }

    // endregion

    // region Event: OnStart

    @Test
    fun `OnStart sets isLoading to true immediately`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Success(mockHeadlines)

        viewModel.event(HeadlinesContract.Event.OnStart)

        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `OnStart loads headlines and clears loading on success`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Success(mockHeadlines)

        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertEquals(mockHeadlines, state.headlines)
    }

    @Test
    fun `OnStart sets isError and clears loading on failure`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Error("Network error")

        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `OnStart does not reload when headlines are already loaded`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Success(mockHeadlines)

        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        coVerify(exactly = 1) { getHeadlines() }
    }

    // endregion

    // region Event: OnSearchQueryChange

    @Test
    fun `OnSearchQueryChange updates searchQuery in state immediately`() = runTest(testDispatcher) {
        viewModel.event(HeadlinesContract.Event.OnSearchQueryChange("kotlin"))

        assertEquals("kotlin", viewModel.state.value.searchQuery)
    }

    @Test
    fun `OnSearchQueryChange filters headlines by title after debounce`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Success(mockHeadlines)
        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        viewModel.event(HeadlinesContract.Event.OnSearchQueryChange("kotlin"))
        advanceTimeBy(600)

        val headlines = viewModel.state.value.headlines
        assertEquals(1, headlines.size)
        assertEquals("Kotlin is great", headlines[0].title)
    }

    @Test
    fun `OnSearchQueryChange filter is case insensitive`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Success(mockHeadlines)
        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        viewModel.event(HeadlinesContract.Event.OnSearchQueryChange("JETPACK"))
        advanceTimeBy(600)

        val headlines = viewModel.state.value.headlines
        assertEquals(1, headlines.size)
        assertEquals("Jetpack Compose tutorial", headlines[0].title)
    }

    @Test
    fun `OnSearchQueryChange returns all headlines when query is blank`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Success(mockHeadlines)
        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        viewModel.event(HeadlinesContract.Event.OnSearchQueryChange("kotlin"))
        advanceTimeBy(600)

        viewModel.event(HeadlinesContract.Event.OnSearchQueryChange(""))
        advanceTimeBy(600)

        assertEquals(mockHeadlines, viewModel.state.value.headlines)
    }

    @Test
    fun `OnSearchQueryChange returns empty list when no match found`() = runTest(testDispatcher) {
        coEvery { getHeadlines() } returns AppResult.Success(mockHeadlines)
        viewModel.event(HeadlinesContract.Event.OnStart)
        advanceUntilIdle()

        viewModel.event(HeadlinesContract.Event.OnSearchQueryChange("nonexistent"))
        advanceTimeBy(600)

        assertTrue(viewModel.state.value.headlines.isEmpty())
    }

    // endregion

    // region Event: OnCardClick

    @Test
    fun `OnCardClick emits NavigateTo effect with correct route`() = runTest(testDispatcher) {
        val article = mockHeadlines[0]

        viewModel.effect.test {
            viewModel.event(HeadlinesContract.Event.OnCardClick(article))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is HeadlinesContract.Effect.NavigateTo)

            val route = (effect as HeadlinesContract.Effect.NavigateTo).route as HeadlineRoute.DetailsRoute
            assertEquals(article.author, route.author)
            assertEquals(article.title, route.title)
            assertEquals(article.description, route.description)
            assertEquals(article.url, route.url)
            assertEquals(article.urlToImage, route.urlToImage)
            assertEquals(article.publishedAt, route.publishedAt)
            assertEquals(article.content, route.content)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion
}

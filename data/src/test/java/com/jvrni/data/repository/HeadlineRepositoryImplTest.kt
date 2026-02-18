package com.jvrni.data.repository

import android.util.Log
import com.jvrni.core.common.AppDispatchers
import com.jvrni.core.common.result.AppResult
import com.jvrni.core.network.api.NewsApiService
import com.jvrni.core.network.dto.HeadlineDto
import com.jvrni.core.network.dto.HeadlineResponseDto
import com.jvrni.core.network.dto.SourceDto
import com.jvrni.data.util.TestAppDispatchers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class HeadlineRepositoryImplTest {

    private lateinit var apiService: NewsApiService
    private lateinit var appDispatchers: AppDispatchers
    private lateinit var repository: HeadlineRepositoryImpl

    private val newsSource = "bbc-news"

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        appDispatchers = TestAppDispatchers()
        apiService = mockk()
        repository = HeadlineRepositoryImpl(apiService, appDispatchers, newsSource)
    }

    @Test
    fun `getHeadlines returns success with correctly mapped domain models`() = runTest {
        // Given
        val responseDto = HeadlineResponseDto(
            status = "ok",
            results = "1",
            articles = listOf(
                HeadlineDto(
                    source = SourceDto(id = "bbc-news", name = "BBC News"),
                    author = "Author",
                    title = "Title",
                    description = "Description",
                    url = "https://example.com",
                    urlToImage = "https://example.com/image.jpg",
                    publishedAt = "2023-06-01T12:00:00Z",
                    content = "Content"
                )
            )
        )
        coEvery { apiService.getHeadlines(newsSource) } returns responseDto

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Success)
        val headlines = (result as AppResult.Success).data
        assertEquals(1, headlines.size)
        assertEquals("Title", headlines[0].title)
        assertEquals("Author", headlines[0].author)
        assertEquals("Description", headlines[0].description)
        assertEquals("https://example.com", headlines[0].url)
        assertEquals("bbc-news", headlines[0].source.id)
        assertEquals("BBC News", headlines[0].source.name)
        coVerify(exactly = 1) { apiService.getHeadlines(newsSource) }
    }

    @Test
    fun `getHeadlines returns success with empty list when no articles`() = runTest {
        // Given
        coEvery { apiService.getHeadlines(newsSource) } returns HeadlineResponseDto(
            status = "ok",
            results = "0",
            articles = emptyList()
        )

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Success)
        assertTrue((result as AppResult.Success).data.isEmpty())
    }

    @Test
    fun `getHeadlines passes newsSource to api`() = runTest {
        // Given
        coEvery { apiService.getHeadlines(newsSource) } returns HeadlineResponseDto(
            status = "ok",
            results = "0",
            articles = emptyList()
        )

        // When
        repository.getHeadlines()

        // Then
        coVerify(exactly = 1) { apiService.getHeadlines(newsSource) }
    }

    @Test
    fun `getHeadlines returns error with message and code on HttpException 401`() = runTest {
        // Given
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 401
        every { httpException.message() } returns "Unauthorized"
        coEvery { apiService.getHeadlines(newsSource) } throws httpException

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals("Unauthorized. Please check your API key.", error.message)
        assertEquals(401, error.code)
    }

    @Test
    fun `getHeadlines returns error with message and code on HttpException 404`() = runTest {
        // Given
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 404
        every { httpException.message() } returns "Not Found"
        coEvery { apiService.getHeadlines(newsSource) } throws httpException

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals("Resource not found", error.message)
        assertEquals(404, error.code)
    }

    @Test
    fun `getHeadlines returns error with message and code on HttpException 500`() = runTest {
        // Given
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 500
        every { httpException.message() } returns "Internal Server Error"
        coEvery { apiService.getHeadlines(newsSource) } throws httpException

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals("Server error. Please try again later.", error.message)
        assertEquals(500, error.code)
    }

    @Test
    fun `getHeadlines returns error on UnknownHostException`() = runTest {
        // Given
        coEvery { apiService.getHeadlines(newsSource) } throws UnknownHostException()

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Error)
        assertEquals(
            "No internet connection. Please check your connection.",
            (result as AppResult.Error).message
        )
    }

    @Test
    fun `getHeadlines returns error on SocketTimeoutException`() = runTest {
        // Given
        coEvery { apiService.getHeadlines(newsSource) } throws SocketTimeoutException()

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Error)
        assertEquals(
            "Request timeout. Please check your connection.",
            (result as AppResult.Error).message
        )
    }

    @Test
    fun `getHeadlines returns error on IOException`() = runTest {
        // Given
        coEvery { apiService.getHeadlines(newsSource) } throws IOException("Network error")

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Error)
        assertEquals(
            "Network error. Please check your connection.",
            (result as AppResult.Error).message
        )
    }

    @Test
    fun `getHeadlines returns error on generic exception`() = runTest {
        // Given
        val errorMessage = "Unexpected error"
        coEvery { apiService.getHeadlines(newsSource) } throws RuntimeException(errorMessage)

        // When
        val result = repository.getHeadlines()

        // Then
        assertTrue(result is AppResult.Error)
        assertEquals(errorMessage, (result as AppResult.Error).message)
    }
}

package com.jvrni.domain

import com.jvrni.core.common.result.AppResult
import com.jvrni.core.common.result.getOrNull
import com.jvrni.domain.models.Headline
import com.jvrni.domain.repository.HeadlineRepository
import com.jvrni.domain.usecase.GetHeadlines
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetHeadlineTest {
    
    private lateinit var repository: HeadlineRepository
    private lateinit var useCase: GetHeadlines
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = GetHeadlines(repository)
    }
    
    @Test
    fun `invoke calls repository and returns success`() = runTest {
        // Given
        val mock = listOf(
            Headline(
                source = mockk(),
                author = "Author",
                title = "Title",
                description = "Description",
                url = "https://example.com",
                urlToImage = "https://example.com/image.jpg",
                publishedAt = "2023-06-01T12:00:00Z",
                content = "Content"
            )
        )
        coEvery { repository.getHeadlines() } returns AppResult.Success(mock)

        // When
        val result = useCase()

        // Then
        assertTrue(result is AppResult.Success)
        assertEquals(1, result.getOrNull()?.size)
        coVerify(exactly = 1) { repository.getHeadlines() }
    }
    
    @Test
    fun `invoke returns failure on repository error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getHeadlines() } returns AppResult.Error(errorMessage)

        // When
        val result = useCase()

        // Then
        assertTrue(result is AppResult.Error)
        assertEquals(errorMessage, (result as AppResult.Error).message)
    }
}
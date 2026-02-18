package com.jvrni.domain.usecase

import com.jvrni.core.common.result.AppResult
import com.jvrni.domain.models.Headline
import com.jvrni.domain.repository.HeadlineRepository
import javax.inject.Inject

class GetHeadline @Inject constructor(
    private val repository: HeadlineRepository
) {
    suspend operator fun invoke(): AppResult<List<Headline>> {
        return repository.getHeadlines()
    }
}
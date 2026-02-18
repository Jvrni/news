package com.jvrni.domain.repository

import com.jvrni.core.common.result.AppResult
import com.jvrni.domain.models.Headline

interface HeadlineRepository {

    suspend fun getHeadlines(): AppResult<List<Headline>>
}
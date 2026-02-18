package com.jvrni.data.repository

import com.jvrni.data.mapper.toDomain
import com.jvrni.data.util.safeApiCall
import com.jvrni.core.common.AppDispatchers
import com.jvrni.core.common.result.AppResult
import com.jvrni.core.network.api.NewsApiService
import com.jvrni.domain.models.Headline
import com.jvrni.domain.repository.HeadlineRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class HeadlineRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val appDispatchers: AppDispatchers,
    @param:Named("NewsSource") private val newsSource: String,
) : HeadlineRepository {

    override suspend fun getHeadlines(): AppResult<List<Headline>> =
        withContext(appDispatchers.io) {
            safeApiCall {
                val response = apiService.getHeadlines(newsSource)
                response.toDomain()
            }
        }
}
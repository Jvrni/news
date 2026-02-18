package com.jvrni.core.network.api

import com.jvrni.core.network.dto.HeadlineResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/top-headlines")
    suspend fun getHeadlines(@Query("sources") source: String): HeadlineResponseDto
}
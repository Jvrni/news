package com.jvrni.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HeadlineResponseDto(
    @field:Json(name = "status")
    val status: String,

    @field:Json(name = "totalResults")
    val results: String,

    @field:Json(name = "articles")
    val articles: List<HeadlineDto>,
)

@JsonClass(generateAdapter = true)
data class HeadlineDto(
    @field:Json(name = "source")
    val source: SourceDto,

    @field:Json(name = "author")
    val author: String,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "description")
    val description: String,

    @field:Json(name = "url")
    val url: String,

    @field:Json(name = "urlToImage")
    val urlToImage: String,

    @field:Json(name = "publishedAt")
    val publishedAt: String,

    @field:Json(name = "content")
    val content: String
)

@JsonClass(generateAdapter = true)
data class SourceDto(

    @field:Json(name = "id")
    val id: String,

    @field:Json(name = "name")
    val name: String,

)
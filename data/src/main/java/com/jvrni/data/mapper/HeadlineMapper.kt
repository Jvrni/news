package com.jvrni.data.mapper

import com.jvrni.core.network.dto.HeadlineDto
import com.jvrni.core.network.dto.HeadlineResponseDto
import com.jvrni.domain.models.Headline

fun HeadlineDto.toDomain(): Headline {
    return Headline(
        source = source.toDomain(),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

fun HeadlineResponseDto.toDomain(): List<Headline> {
    return articles.map { dto ->
        dto.toDomain()
    }
}

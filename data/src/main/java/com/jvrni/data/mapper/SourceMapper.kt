package com.jvrni.data.mapper

import com.jvrni.core.network.dto.SourceDto
import com.jvrni.domain.models.Source

fun SourceDto.toDomain(): Source {
    return Source(
        id = id,
        name = name
    )
}

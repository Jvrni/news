package com.jvrni.core.common.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val publishedFormatter = DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale.ENGLISH)

fun String.formatPublishedAt(): String {
    return runCatching {
        Instant.parse(this)
            .atZone(ZoneId.systemDefault())
            .format(publishedFormatter)
    }.getOrElse { this }
}
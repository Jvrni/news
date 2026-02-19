package com.jvrni.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object SplashRoute : Route

@Serializable
data object HeadlineRoute : Route {

    @Serializable
    data object ListRoute : Route

    @Serializable
    data class DetailsRoute(
        val author: String,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String,
        val publishedAt: String,
        val content: String
    ) : Route
}

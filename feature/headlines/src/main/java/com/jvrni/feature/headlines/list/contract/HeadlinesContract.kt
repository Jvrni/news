package com.jvrni.feature.headlines.list.contract

import com.jvrni.core.navigation.Route
import com.jvrni.core.ui.utils.UnidirectionalViewModel
import com.jvrni.domain.models.Headline

interface HeadlinesContract :
    UnidirectionalViewModel<HeadlinesContract.State, HeadlinesContract.Event, HeadlinesContract.Effect> {

    data class State(
        val title: String,
        val searchQuery: String = "",
        val headlines: List<Headline> = emptyList(),
        val isLoading: Boolean = false,
        val isError: Boolean = false,
    )

    sealed class Event {
        data object OnStart : Event()
        data class OnSearchQueryChange(val query: String) : Event()
        data class OnCardClick(val article: Headline) : Event()
    }

    sealed class Effect {
        data class NavigateTo(val route: Route) : Effect()
    }
}
package com.jvrni.feature.headlines.list.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.jvrni.core.common.result.onError
import com.jvrni.core.common.result.onSuccess
import com.jvrni.core.navigation.HeadlineRoute
import com.jvrni.domain.models.Headline
import com.jvrni.domain.usecase.GetHeadlines
import com.jvrni.feature.headlines.list.contract.HeadlinesContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HeadlinesViewModel @Inject constructor(
    private val getHeadlines: GetHeadlines,
    private val imageLoader: ImageLoader,
    @param:ApplicationContext private val context: Context,
    @param:Named("AppName") private val title: String
) : ViewModel(), HeadlinesContract {

    private val _state = MutableStateFlow(HeadlinesContract.State(title))
    override val state: StateFlow<HeadlinesContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HeadlinesContract.Effect>()
    override val effect: SharedFlow<HeadlinesContract.Effect> = _effect.asSharedFlow()

    private val allHeadlines = MutableStateFlow<List<Headline>>(emptyList())
    private val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(
                searchQuery
                    .debounce(500)
                    .distinctUntilChanged()
                    .onStart { emit("") },
                allHeadlines
            ) { query, headlines ->
                filterHeadlines(query, headlines)
            }
                .distinctUntilChanged()
                .collect { filtered ->
                    _state.update { it.copy(headlines = filtered) }
                }
        }
    }

    override fun event(event: HeadlinesContract.Event) {
        when (event) {
            is HeadlinesContract.Event.OnStart -> onStart()
            is HeadlinesContract.Event.OnSearchQueryChange -> onSearchQueryChange(event.query)
            is HeadlinesContract.Event.OnCardClick -> onCardClick(event.article)
        }
    }

    private fun onStart() {
        if (allHeadlines.value.isNotEmpty()) return

        _state.update { it.copy(isLoading = true, isError = false) }

        viewModelScope.launch {
            getHeadlines.invoke()
                .onSuccess { result ->
                    allHeadlines.value = result
                    preloadImages(result)
                }.onError {
                    _state.update { it.copy(isError = true) }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun preloadImages(headlines: List<Headline>) {
        headlines.forEach { headline ->
            val request = ImageRequest.Builder(context)
                .data(headline.urlToImage)
                .allowHardware(false)
                .build()
            imageLoader.enqueue(request)
        }
    }

    private fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        _state.update { it.copy(searchQuery = query) }
    }

    private fun onCardClick(article: Headline) {
        viewModelScope.launch {
            _effect.emit(
                HeadlinesContract.Effect.NavigateTo(
                    HeadlineRoute.DetailsRoute(
                        author = article.author,
                        title = article.title,
                        description = article.description,
                        url = article.url,
                        urlToImage = article.urlToImage,
                        publishedAt = article.publishedAt,
                        content = article.content
                    )
                )
            )
        }
    }

    private fun filterHeadlines(query: String, headlines: List<Headline>): List<Headline> {
        if (query.isBlank()) return headlines

        return headlines.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }
}

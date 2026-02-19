package com.jvrni.feature.headlines.list.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jvrni.core.ui.components.EmptyResultsState
import com.jvrni.core.ui.components.ErrorState
import com.jvrni.core.ui.components.LoadingScreen
import com.jvrni.feature.headlines.list.contract.HeadlinesContract
import com.jvrni.feature.headlines.list.ui.components.Card
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.spring
import androidx.compose.ui.tooling.preview.Preview
import com.jvrni.core.ui.theme.NewsAppTheme
import com.jvrni.domain.models.Headline
import com.jvrni.domain.models.Source
import com.jvrni.feature.headlines.list.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HeadlinesScreen(
    uiState: HeadlinesContract.State,
    event: (HeadlinesContract.Event) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    paddingValues: PaddingValues,
    boundsTransform: BoundsTransform,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(top = paddingValues.calculateTopPadding() + 6.dp, bottom = paddingValues.calculateBottomPadding())
    ) {
        TopBar(uiState.title, uiState.searchQuery) { query ->
            event.invoke(HeadlinesContract.Event.OnSearchQueryChange(query))
        }

        when {
            uiState.isLoading -> LoadingScreen(modifier = Modifier.weight(1f))
            uiState.isError -> ErrorState(modifier = Modifier.weight(1f)) {
                event.invoke(HeadlinesContract.Event.OnStart)
            }
            uiState.headlines.isEmpty() -> EmptyResultsState(modifier = Modifier.weight(1f))
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    items(uiState.headlines, key = { it.url }) { article ->
                        Card(
                            article = article,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform,
                            onClick = { event.invoke(HeadlinesContract.Event.OnCardClick(article)) }
                        )
                    }
                }
            }
        }
    }
}

private val previewHeadlines = listOf(
    Headline(
        source = Source(id = "bbc-news", name = "BBC News", category = null),
        author = "Jane Smith",
        title = "Global markets surge as tech stocks hit record highs worldwide",
        description = "Stock markets around the world saw significant gains.",
        url = "https://example.com/1",
        urlToImage = "",
        publishedAt = "19 Feb, 09:00 AM",
        content = ""
    ),
    Headline(
        source = Source(id = "bbc-news", name = "BBC News", category = null),
        author = "John Doe",
        title = "Scientists discover new species of marine life in the Pacific Ocean",
        description = "A research team has identified a previously unknown creature.",
        url = "https://example.com/2",
        urlToImage = "",
        publishedAt = "19 Feb, 08:30 AM",
        content = ""
    ),
    Headline(
        source = Source(id = "bbc-news", name = "BBC News", category = null),
        author = "",
        title = "City council approves major infrastructure investment plan for 2026",
        description = "Local government commits to modernizing public transport.",
        url = "https://example.com/3",
        urlToImage = "",
        publishedAt = "19 Feb, 07:45 AM",
        content = ""
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Preview(showSystemUi = true, name = "Headlines — With Data")
@Composable
private fun PreviewHeadlinesScreenWithData() {
    NewsAppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                HeadlinesScreen(
                    uiState = HeadlinesContract.State(title = "BBC", headlines = previewHeadlines),
                    event = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    paddingValues = PaddingValues(),
                    boundsTransform = { _, _ -> spring() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Preview(showSystemUi = true, name = "Headlines — Loading")
@Composable
private fun PreviewHeadlinesScreenLoading() {
    NewsAppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                HeadlinesScreen(
                    uiState = HeadlinesContract.State(title = "BBC", isLoading = true),
                    event = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    paddingValues = PaddingValues(),
                    boundsTransform = { _, _ -> spring() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Preview(showSystemUi = true, name = "Headlines — Error")
@Composable
private fun PreviewHeadlinesScreenError() {
    NewsAppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                HeadlinesScreen(
                    uiState = HeadlinesContract.State(title = "BBC", isError = true),
                    event = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    paddingValues = PaddingValues(),
                    boundsTransform = { _, _ -> spring() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Preview(showSystemUi = true, name = "Headlines — Empty")
@Composable
private fun PreviewHeadlinesScreenEmpty() {
    NewsAppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                HeadlinesScreen(
                    uiState = HeadlinesContract.State(title = "BBC", headlines = emptyList()),
                    event = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    paddingValues = PaddingValues(),
                    boundsTransform = { _, _ -> spring() }
                )
            }
        }
    }
}

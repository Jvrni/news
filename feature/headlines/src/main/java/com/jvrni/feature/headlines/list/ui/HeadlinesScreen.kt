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

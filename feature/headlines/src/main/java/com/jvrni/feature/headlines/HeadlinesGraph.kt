package com.jvrni.feature.headlines

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Rect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jvrni.core.navigation.HeadlineRoute
import com.jvrni.core.ui.utils.collectInLaunchedEffect
import com.jvrni.core.ui.utils.use
import com.jvrni.feature.headlines.detail.ui.HeadlineDetailScreen
import com.jvrni.feature.headlines.list.contract.HeadlinesContract
import com.jvrni.feature.headlines.list.ui.HeadlinesScreen
import com.jvrni.feature.headlines.list.ui.HeadlinesViewModel

private const val ENTER_DURATION = 350
private const val EXIT_DURATION = 550

private fun boundsSpec(initialBounds: Rect, targetBounds: Rect): TweenSpec<Rect> {
    val isGoingBack = targetBounds.width < initialBounds.width
    return tween(
        durationMillis = if (isGoingBack) EXIT_DURATION else ENTER_DURATION,
        easing = FastOutSlowInEasing
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.headlinesGraph(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    paddingValues: PaddingValues
) {
    composable<HeadlineRoute.ListRoute>(
        enterTransition = { fadeIn(tween(ENTER_DURATION)) },
        exitTransition = { fadeOut(tween(ENTER_DURATION)) },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { fadeOut(tween(EXIT_DURATION)) }
    ) {
        val viewModel = hiltViewModel<HeadlinesViewModel>()
        val (uiState, event, effect) = use(viewModel = viewModel)

        effect.collectInLaunchedEffect { dispatch ->
            when (dispatch) {
                is HeadlinesContract.Effect.NavigateTo -> navController.navigate(dispatch.route)
            }
        }

        LaunchedEffect(Unit) {
            event.invoke(HeadlinesContract.Event.OnStart)
        }

        HeadlinesScreen(
            uiState = uiState,
            event = event,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            paddingValues = paddingValues,
            boundsTransform = { initial, target -> boundsSpec(initial, target) }
        )
    }

    composable<HeadlineRoute.DetailsRoute>(
        enterTransition = { fadeIn(tween(ENTER_DURATION)) },
        exitTransition = { fadeOut(tween(ENTER_DURATION)) },
        popEnterTransition = { fadeIn(tween(EXIT_DURATION)) },
        popExitTransition = { fadeOut(tween(EXIT_DURATION)) }
    ) {
        val route = it.toRoute<HeadlineRoute.DetailsRoute>()

        HeadlineDetailScreen(
            route = route,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            paddingValues = paddingValues,
            boundsTransform = { initial, target -> boundsSpec(initial, target) },
            onBack = { navController.popBackStack() }
        )
    }
}

package com.jvrni.news.ui

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jvrni.core.navigation.SplashRoute
import com.jvrni.feature.headlines.headlinesGraph
import com.jvrni.feature.splash.splashGraph

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun App() {
    val navController = rememberNavController()

    Scaffold { paddingValues ->
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = SplashRoute,
            ) {
                splashGraph(navController)
                headlinesGraph(navController, this@SharedTransitionLayout, paddingValues)
            }
        }
    }
}

package com.jvrni.feature.splash

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jvrni.core.navigation.SplashRoute
import com.jvrni.core.ui.utils.collectInLaunchedEffect
import com.jvrni.core.ui.utils.showBiometricPrompt
import com.jvrni.core.ui.utils.use
import com.jvrni.feature.splash.contract.SplashContract
import com.jvrni.feature.splash.ui.SplashScreen
import com.jvrni.feature.splash.ui.SplashViewModel

fun NavGraphBuilder.splashGraph(navController: NavHostController) {
    composable<SplashRoute> {
        val viewModel = hiltViewModel<SplashViewModel>()
        val (_, event, effect) = use(viewModel = viewModel)
        val context = LocalContext.current

        effect.collectInLaunchedEffect { dispatch ->
            when (dispatch) {
                is SplashContract.Effect.NavigateTo -> navController.navigate(dispatch.route) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
                is SplashContract.Effect.ShowBiometricPrompt -> {
                    val activity = context as FragmentActivity
                    showBiometricPrompt(
                        activity = activity,
                        context = context,
                        onSuccess = { event.invoke(SplashContract.Event.OnBiometricResult(isSuccess = true)) },
                        onError = { activity.finish() }
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            event.invoke(SplashContract.Event.OnStart)
        }

        SplashScreen()
    }
}

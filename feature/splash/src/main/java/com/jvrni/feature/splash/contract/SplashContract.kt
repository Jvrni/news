package com.jvrni.feature.splash.contract

import com.jvrni.core.navigation.Route
import com.jvrni.core.ui.utils.UnidirectionalViewModel

interface SplashContract :
    UnidirectionalViewModel<SplashContract.State, SplashContract.Event, SplashContract.Effect> {

    data object State

    sealed class Event {
        data object OnStart : Event()
        data class OnBiometricResult(val isSuccess: Boolean) : Event()
    }

    sealed class Effect {
        data class NavigateTo(val route: Route) : Effect()
        data object ShowBiometricPrompt : Effect()
    }
}

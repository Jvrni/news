package com.jvrni.feature.splash.ui

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvrni.core.navigation.HeadlineRoute
import com.jvrni.feature.splash.contract.SplashContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SPLASH_DELAY_MS = 2000L

@HiltViewModel
class SplashViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel(), SplashContract {

    private val _state = MutableStateFlow(SplashContract.State)
    override val state: StateFlow<SplashContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SplashContract.Effect>()
    override val effect: SharedFlow<SplashContract.Effect> = _effect.asSharedFlow()

    override fun event(event: SplashContract.Event) {
        when (event) {
            is SplashContract.Event.OnStart -> onStart()
            is SplashContract.Event.OnBiometricResult -> onBiometricResult(event.isSuccess)
        }
    }

    private fun onStart() {
        viewModelScope.launch {
            delay(SPLASH_DELAY_MS)
            val canAuthenticate = BiometricManager.from(context)
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                _effect.emit(SplashContract.Effect.ShowBiometricPrompt)
            } else {
                _effect.emit(SplashContract.Effect.NavigateTo(HeadlineRoute.ListRoute))
            }
        }
    }

    private fun onBiometricResult(isSuccess: Boolean) {
        if (isSuccess) {
            viewModelScope.launch {
                _effect.emit(SplashContract.Effect.NavigateTo(HeadlineRoute.ListRoute))
            }
        }
    }
}

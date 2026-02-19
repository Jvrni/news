package com.jvrni.core.ui.utils

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.jvrni.core.ui.R

fun showBiometricPrompt(
    activity: FragmentActivity,
    context: Context,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(context.getString(R.string.biometric_title))
        .setSubtitle(context.getString(R.string.biometric_subtitle))
        .setNegativeButtonText(context.getString(R.string.biometric_negative_button))
        .build()

    BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(context),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onError()
            }

            override fun onAuthenticationFailed() = Unit
        }
    ).authenticate(promptInfo)
}

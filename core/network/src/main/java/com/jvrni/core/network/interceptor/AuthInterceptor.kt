package com.jvrni.core.network.interceptor

import com.jvrni.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .header("X-Api-Key", BuildConfig.API_KEY)
            .build()

        return chain.proceed(newRequest)
    }
}
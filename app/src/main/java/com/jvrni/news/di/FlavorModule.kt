package com.jvrni.news.di

import com.jvrni.news.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule {
    @Provides
    @Singleton
    @Named("NewsSource")
    fun provideNewsSource(): String = BuildConfig.NEWS_SOURCE

    @Provides
    @Singleton
    @Named("AppName")
    fun provideAppName(): String = when (BuildConfig.NEWS_SOURCE) {
        "bbc-news" -> "BBC"
        "cnn" -> "CNN"
        else -> "News"
    }
}
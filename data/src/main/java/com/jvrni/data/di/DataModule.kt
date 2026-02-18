package com.jvrni.data.di

import com.jvrni.data.repository.HeadlineRepositoryImpl
import com.jvrni.domain.repository.HeadlineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    
    @Binds
    @Singleton
    abstract fun bindHeadlineRepository(
        headlineRepositoryImpl: HeadlineRepositoryImpl
    ): HeadlineRepository
}
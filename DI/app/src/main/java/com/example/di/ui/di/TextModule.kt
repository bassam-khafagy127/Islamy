package com.example.di.ui.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TextModule {
    @Provides
    @Named("First")
    fun provideStringMessageOne(): String {
        return "Hi Iam First Message"
    }
    @Provides
    @Named("Second")
    fun provideStringMessageTwo(): String {
        return "Hi Iam Second Message"
    }
}
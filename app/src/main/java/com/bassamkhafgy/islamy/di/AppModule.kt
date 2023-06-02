package com.bassamkhafgy.islamy.di

import android.content.Context
import com.bassamkhafgy.islamy.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHomeRepository(@ApplicationContext appContext: Context): HomeRepository {
        return HomeRepository(appContext)
    }
}
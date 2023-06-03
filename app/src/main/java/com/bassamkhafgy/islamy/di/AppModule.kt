package com.bassamkhafgy.islamy.di

import android.content.Context
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    fun provideHomeRepository(
        @ApplicationContext appContext: Context,
        fusedLocationProviderClient: FusedLocationProviderClient
    ): HomeRepository {
        return HomeRepository(appContext, fusedLocationProviderClient)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(appContext)
    }
}
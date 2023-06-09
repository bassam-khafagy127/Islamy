package com.bassamkhafgy.islamy.di

import android.app.Application
import android.content.Context
import com.bassamkhafgy.islamy.data.database.IslamyAppDataBase
import com.bassamkhafgy.islamy.networking.TimeApiService
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.repository.SearchRepository
import com.bassamkhafgy.islamy.utill.Constants.LANGUAGENAVIGATION.LANGUAGE_SHARED_PREFERENCE

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
        fusedLocationProviderClient: FusedLocationProviderClient,
        timeApiService: TimeApiService,
        timingsDataBase: IslamyAppDataBase
    ): HomeRepository {
        return HomeRepository(
            appContext,
            fusedLocationProviderClient,
            timeApiService,
            timingsDataBase
        )
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        timeApiService: TimeApiService, @ApplicationContext appContext: Context
    ): SearchRepository {
        return SearchRepository(timeApiService, appContext)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(appContext)
    }

    @Provides
    fun provideIntroductionViewModel(application: Application) =
        application.getSharedPreferences(LANGUAGE_SHARED_PREFERENCE, Context.MODE_PRIVATE)

}
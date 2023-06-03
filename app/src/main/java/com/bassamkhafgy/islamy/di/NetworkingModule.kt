package com.bassamkhafgy.islamy.di

import com.bassamkhafgy.islamy.networking.TimeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {
    @Singleton
    @Provides
    fun providePrayerApiService(): TimeApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TimeApiService::class.java)
    }
}

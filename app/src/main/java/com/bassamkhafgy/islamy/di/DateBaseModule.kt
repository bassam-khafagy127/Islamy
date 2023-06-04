package com.bassamkhafgy.islamy.di

import android.content.Context
import androidx.room.Room
import com.bassamkhafgy.islamy.data.database.TimingsDataBase
import com.bassamkhafgy.islamy.utill.Constants.DATA_BASE.TIMINGS_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DateBaseModule {
    @Provides
    fun provideTimingDataBase(@ApplicationContext context: Context): TimingsDataBase {
        return Room.databaseBuilder(context,TimingsDataBase::class.java,TIMINGS_DATABASE_NAME).build()
    }
}
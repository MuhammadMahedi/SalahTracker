package com.example.salahtracker.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.salahtracker.data.local.SalahDB
import com.example.salahtracker.data.local.SalahDao
import com.example.salahtracker.domain.repository.SalahRepository
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
    fun provideDatabase(@ApplicationContext context: Context): SalahDB {
        return Room.databaseBuilder(
            context, SalahDB::class.java, "salah_db"
        ).build()
    }

    @Provides
    fun provideSalahDao(db: SalahDB) = db.dao

    @Provides
    fun providesTaskRepository(salahDao: SalahDao) = SalahRepository(salahDao)


}
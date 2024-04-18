package com.aidevu.signage.di

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.aidevu.signage.adapter.db.ad.AdDao
import com.aidevu.signage.adapter.db.ad.AdDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModuleTable {

    private val tableDataBaseName = "AdDB"

    @Provides
    @Singleton
    fun provideTableResultDatabase(application: Application): AdDatabase {
        return databaseBuilder(application, AdDatabase::class.java, tableDataBaseName)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideTableResultDao(adDatabase: AdDatabase): AdDao {
        return adDatabase.adDao()
    }
}
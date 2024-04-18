package com.aidevu.signage.adapter.db.ad

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Ad::class], version = 1, exportSchema = false)
abstract class AdDatabase : RoomDatabase() {
    abstract fun adDao(): AdDao
}
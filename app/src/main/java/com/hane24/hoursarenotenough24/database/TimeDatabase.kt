package com.hane24.hoursarenotenough24.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hane24.hoursarenotenough24.App

@Database(entities = [TimeDatabaseDto::class], version = 1)
abstract class TimeDatabase: RoomDatabase() {
    abstract fun timeDatabaseDAO(): TimeDatabaseDAO
}

private lateinit var INSTANCE: TimeDatabase
fun createDatabase(): TimeDatabase {
    synchronized(TimeDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(App.instance.applicationContext, TimeDatabase::class.java, "time_database").build()
        }
    }
    return INSTANCE
}
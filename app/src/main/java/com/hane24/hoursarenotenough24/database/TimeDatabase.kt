package com.hane24.hoursarenotenough24.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TimeDatabaseDto::class], version = 1)
abstract class TimeDatabase: RoomDatabase() {
    abstract fun timeDatabaseDAO(): TimeDatabaseDAO
}
package com.hane24.hoursarenotenough24.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hane24.hoursarenotenough24.App

@Database(
    version = 2,
    entities = [TagLogDto::class, AccumulationTimeDto::class],
)
abstract class TimeDatabase : RoomDatabase() {
    abstract fun timeDatabaseDAO(): TimeDatabaseDAO
}

private lateinit var INSTANCE: TimeDatabase
fun createDatabase(): TimeDatabase {
    synchronized(TimeDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                App.instance.applicationContext,
                TimeDatabase::class.java,
                "time_database"
            ).addMigrations(MIGRATION_1_2).build()
        }
    }
    return INSTANCE
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `accumulation_time`")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `accumulation_time` " +
                    "(`date` TEXT NOT NULL, " +
                    "`totalAccumulationTime` INTEGER, " +
                    "`acceptedAccumulationTime` INTEGER, " +
                    "PRIMARY KEY(`date`))"
        )
    }
}

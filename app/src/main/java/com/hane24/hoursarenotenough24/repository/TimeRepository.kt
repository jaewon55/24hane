package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.network.InOutTimeContainer
import com.hane24.hoursarenotenough24.network.asDatabaseDto

class TimeRepository(private val db: TimeDatabase) {
    fun getAll() {
        db.timeDatabaseDAO().getAll()
    }

    fun getMonth(date: String) {
        db.timeDatabaseDAO().getMonth(date)
    }

    fun getDay(date: String) {
        db.timeDatabaseDAO().getDay(date)
    }

    fun insert(timeInfo: InOutTimeContainer) {
        val lst = timeInfo.asDatabaseDto().toTypedArray()

        db.timeDatabaseDAO().insertAll(*lst)
    }

    fun deleteAll() {
        db.timeDatabaseDAO().deleteAll()
    }
}
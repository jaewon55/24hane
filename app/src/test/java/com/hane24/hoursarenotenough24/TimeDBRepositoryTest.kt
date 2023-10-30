package com.hane24.hoursarenotenough24

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimeDBRepositoryTest {
    private lateinit var timeDBRepository: TimeDBRepository
    private lateinit var context: Context
    private lateinit var db: TimeDatabase

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, TimeDatabase::class.java).build()
        timeDBRepository = TimeDBRepository(db)
    }

    @Test
    fun `읽기 테스트`() = runTest {
        //given
        val timeDto = TimeDatabaseDto(
            "20231030",
            100,
            1001,
            100,
            System.currentTimeMillis()
        )

        db.timeDatabaseDAO().insertAll(timeDto)

        //when
        val result = timeDBRepository.getTimeByMonth(2023, 10, "123")

        //then
        Assert.assertEquals(timeDto, result.firstOrNull())
    }

}
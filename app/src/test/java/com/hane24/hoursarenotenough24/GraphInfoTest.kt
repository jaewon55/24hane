package com.hane24.hoursarenotenough24

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.hane24.hoursarenotenough24.overview.GraphInfo
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner.*
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@Suppress("NonAsciiCharacters")
@RunWith(RobolectricTestRunner::class)
class GraphInfoTes {
    private lateinit var context: Context

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun `6주 or 6달 총 시간 계산 테스트`() {
        //given
        val accumulationTime = 3600 * 10 // 10시간
        val accumulationTimes = List(6) { accumulationTime.toLong() + (3600 * it) } // [10h, 11h, 12h, 13h, 14h, 15h]
        val graphInfo = GraphInfo(accumulationTimes, false)

        //when
        val result = graphInfo.calculateTotalTime(3)

        //then
        Assert.assertEquals(13.0, result, 0.0001)
    }

    @Test
    fun `6주 or 6달 평균 시간 계산 테스트`() {
        //given
        val accumulationTime = 3600 * 14 // 14시간
        val accumulationTimes = List(6) { accumulationTime.toLong() } // [14h, 14h, 14h, 14h, 14h, 14h]
        val graphInfo = GraphInfo(accumulationTimes, false)

        //when
        val result = graphInfo.calculateAverageTime(0)

        //then
        Assert.assertEquals(2.0, result, 0.0001)
    }

    @Test
    fun `6주 기준 날짜 파싱 테스트`() {
        //given
        val graphInfo = GraphInfo(List(6) { 0 }, false)
        val date = Calendar.getInstance()
        date.set(2023, Calendar.NOVEMBER, 1)

        //when
        val result = graphInfo.parseDateText(0, date)

        //then
        Assert.assertEquals("10.30(월) - 11.5(일)", result)
    }

    @Test
    fun `6달 기준 날짜 파싱 테스트`() {
        //given
        val graphInfo = GraphInfo(List(6) { 0 }, true)
        val date = Calendar.getInstance()
        date.set(2023, Calendar.NOVEMBER, 1)

        //when
        val result = graphInfo.parseDateText(0, date)

        //then
        Assert.assertEquals("2023.11", result)
    }

    @Test
    fun `높이 계산 테스트`() {
        //given
        val graphInfo = GraphInfo(List(6) { it + 1L }, true)
        val expected = (77 * (100 / 6) * 0.01 + 10).toInt()

        //when
        val result = graphInfo.calculateGraphHeight(0, 1.0f)

        //then
        Assert.assertEquals(expected, result)
    }
}
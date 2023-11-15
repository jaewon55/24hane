package com.hane24.hoursarenotenough24

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.inoutlog.GetLogsUseCase
import com.hane24.hoursarenotenough24.inoutlog.LogViewModel
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

private const val ACCESS_TOKEN = "token"

@RunWith(RobolectricTestRunner::class)
class LogViewModelTest {

    private lateinit var context: Context
    private lateinit var db: TimeDatabase
    private lateinit var timeServerRepository: TimeServerRepository
    private lateinit var timeDBRepository: TimeDBRepository
    private lateinit var getLogsUseCase: GetLogsUseCase
    private lateinit var viewModel: LogViewModel
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtilss

    @Mock
    private lateinit var mockHane24Api: Hane24Api


    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, TimeDatabase::class.java).build()
        sharedPreferenceUtils = SharedPreferenceUtilss.initialize(context)
        timeServerRepository = TimeServerRepository(mockHane24Api, sharedPreferenceUtils)
        timeDBRepository = TimeDBRepository(db)
        getLogsUseCase = GetLogsUseCase(timeServerRepository, timeDBRepository)
        viewModel = LogViewModel(timeServerRepository, timeDBRepository)
    }

    @Test
    fun serverRepositoryTest() = runTest {
        // given
        sharedPreferenceUtils.saveAccessToken(ACCESS_TOKEN)

        // when
        `when`(mockHane24Api.getAllTagPerMonth(sharedPreferenceUtils.getAccessToken(), 2023, 11))
            .thenAnswer { MonthLogsData.data202311 }

        val result = timeServerRepository.getTagLogPerMonth(2023, 11, ACCESS_TOKEN)
        // then
        Assert.assertEquals(MonthLogsData.data202311.inOutLogs, result)
    }

    @Test
    fun `getLogsUseCaseTest`() = runTest {
        // given
        sharedPreferenceUtils.saveAccessToken(ACCESS_TOKEN)

        // when
        `when`(mockHane24Api.getAllTagPerMonth(sharedPreferenceUtils.getAccessToken(), 2023, 11))
            .thenAnswer { MonthLogsData.data202311 }
        val result = getLogsUseCase(2023, 11)

        // then
        Assert.assertEquals(MonthLogsData.data202311.inOutLogs, result)

    }

    @Test
    fun `log 갱신 테스트`() = runTest {
        // given
        sharedPreferenceUtils.saveAccessToken(ACCESS_TOKEN)

        // when
        `when`(mockHane24Api.getAllTagPerMonth(sharedPreferenceUtils.getAccessToken(), 2023, 11))
            .thenAnswer { MonthLogsData.data202311 }
        `when`(mockHane24Api.getAllTagPerMonth(sharedPreferenceUtils.getAccessToken(), 2023, 10))
            .thenAnswer { MonthLogsData.data202311 }

        viewModel.reloadLogs(2023, 11)
        viewModel.reloadLogs(2023, 10)

        val result = viewModel.tagLogs

        // then
        verify(mockHane24Api, times(1))
            .getAllTagPerMonth(sharedPreferenceUtils.getAccessToken(), 2023, 10)
        Assert.assertEquals(2023, viewModel.year)
        Assert.assertEquals(10, viewModel.month)
        Assert.assertEquals(MonthLogsData.data202311.inOutLogs, result)
    }
}
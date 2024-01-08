package com.hane24.hoursarenotenough24

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.network.InfoMessage
import com.hane24.hoursarenotenough24.network.InfoMessages
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.ui.home.OverViewModelFactory
import com.hane24.hoursarenotenough24.ui.home.OverViewViewModel
import com.hane24.hoursarenotenough24.repository.UserRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner


@Suppress("NonAsciiCharacters")
@RunWith(RobolectricTestRunner::class)
class OverViewModelTest {
    @get:Rule
    @ExperimentalCoroutinesApi
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: OverViewViewModel
    private lateinit var viewModelFactory: OverViewModelFactory
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var userRepository: UserRepository
    private lateinit var context: Context

    @Mock
    private lateinit var hane24Api: Hane24Api

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().context
        sharedPreferenceUtils = SharedPreferenceUtils.initialize(context)
        userRepository = UserRepository(hane24Api, sharedPreferenceUtils)
        viewModelFactory = OverViewModelFactory(sharedPreferenceUtils, userRepository)
        viewModel = viewModelFactory.create(OverViewViewModel::class.java)
        Mockito.reset(hane24Api)
    }

    @Test
    fun `MainInfo 갱신 테스트`() = runTest {
        //given
        sharedPreferenceUtils.saveAccessToken("123")

        //when
        Mockito.`when`(hane24Api.getMainInfo(sharedPreferenceUtils.getAccessToken())).thenAnswer {
            MainInfo(
                "test",
                "test",
                "IN",
                "",
                10,
                InfoMessages(InfoMessage("", ""), InfoMessage("", "")))
        }
        viewModel.refresh()

        //then
        Mockito.verify(hane24Api, times(1))
            .getMainInfo(sharedPreferenceUtils.getAccessToken())
        Assert.assertEquals("test", viewModel.intraId.first())
        Assert.assertEquals("test", viewModel.profileImageUrl.first())
        Assert.assertEquals(true, viewModel.inOutState.first())
        Assert.assertEquals(10, viewModel.populationGaepo.first())
    }

    @Test
    fun `AccumulationTime 갱신 테스트`() = runTest {
        //given
        sharedPreferenceUtils.saveAccessToken("123")
        val accTime = AccumulationTimeInfo(
            7200,
            7200 * 4,
            listOf(1L, 1L, 1L, 1L, 1L, 1L),
            listOf(1L, 1L, 1L, 1L, 1L, 1L),
            7200 * 4
        )

        //when
        Mockito.`when`(hane24Api.getAccumulationTime(sharedPreferenceUtils.getAccessToken()))
            .thenAnswer {
                accTime
            }
        viewModel.refresh()

        //then
        Mockito.verify(hane24Api, times(1))
            .getAccumulationTime(sharedPreferenceUtils.getAccessToken())
        Assert.assertEquals(accTime, viewModel.accumulationTime.first())
        Assert.assertEquals("2" to "0", viewModel.dayAccumulationTime.first())
        Assert.assertEquals("8" to "0", viewModel.monthAccumulationTime.first())
        Assert.assertEquals("8" to "0", viewModel.acceptedAccumulationTime.first())
    }

    @After
    fun after() {
        Mockito.reset(hane24Api)
    }
}
package com.hane24.hoursarenotenough24

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.overview.OverViewModelFactory
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.repository.UserRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
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
    private lateinit var sharedPreferenceUtilss: SharedPreferenceUtilss
    private lateinit var userRepository: UserRepository
    private lateinit var context: Context
    @Mock
    private lateinit var hane24Api: Hane24Api

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().context
        sharedPreferenceUtilss = SharedPreferenceUtilss.initialize(context)
        userRepository = UserRepository(hane24Api, sharedPreferenceUtilss)
        viewModelFactory = OverViewModelFactory(sharedPreferenceUtilss, userRepository)
        viewModel = viewModelFactory.create(OverViewViewModel::class.java)
        Mockito.reset(hane24Api)
    }

    @Test
    fun `MainInfo 갱신 테스트`() = runTest {
        //given
        sharedPreferenceUtilss.saveAccessToken("123")

        //when
        Mockito.`when`(hane24Api.getMainInfo(sharedPreferenceUtilss.getAccessToken())).thenAnswer {
            MainInfo("test", "test", "IN", "", 10, 10)
        }
        viewModel.refresh()

        //then
        Mockito.verify(hane24Api, times(1))
            .getMainInfo(sharedPreferenceUtilss.getAccessToken())
        Assert.assertEquals("test", viewModel.intraId.first())
        Assert.assertEquals("test", viewModel.profileImageUrl.first())
        Assert.assertEquals(true, viewModel.inOutState.first())
        Assert.assertEquals(10 to 10, viewModel.populationSeochoAndGaepo.first())
    }

    @Test
    fun `AccumulationTime 갱신 테스트`() = runTest {
        //given
        sharedPreferenceUtilss.saveAccessToken("123")
        val accTime = AccumulationTimeInfo(
            7200,
            7200 * 4,
            listOf(1L, 1L, 1L, 1L, 1L, 1L),
            listOf(1L, 1L, 1L, 1L, 1L, 1L)
        )

        //when
        Mockito.`when`(hane24Api.getAccumulationTime(sharedPreferenceUtilss.getAccessToken())).thenAnswer {
            accTime
        }
        viewModel.refresh()

        //then
        Mockito.verify(hane24Api, times(1))
            .getAccumulationTime(sharedPreferenceUtilss.getAccessToken())
        Assert.assertEquals(accTime, viewModel.accumulationTime.first())
        Assert.assertEquals("2" to "0", viewModel.dayAccumulationTime.first())
        Assert.assertEquals("8" to "0", viewModel.monthAccumulationTime.first())
    }

    @After
    fun after() {
        Mockito.reset(hane24Api)
    }
}
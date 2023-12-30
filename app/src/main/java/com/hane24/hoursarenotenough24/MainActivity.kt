package com.hane24.hoursarenotenough24

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hane24.hoursarenotenough24.database.createDatabase
import com.hane24.hoursarenotenough24.inoutlog.LogViewModel
import com.hane24.hoursarenotenough24.inoutlog.LogViewModelFactory
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.overview.OverViewModelFactory
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.reissue.ReissueViewModelFactory
import com.hane24.hoursarenotenough24.reissue.ReissueViewModel
import com.hane24.hoursarenotenough24.repository.ReissueRepository
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository
import com.hane24.hoursarenotenough24.repository.UserRepository
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val overViewViewModel: OverViewViewModel by viewModels {
        OverViewModelFactory(
            App.sharedPreferenceUtils,
            UserRepository(Hane24Apis.hane24ApiService, App.sharedPreferenceUtils)
        )
    }

    private val logViewModel: LogViewModel by viewModels {
        LogViewModelFactory(
            TimeServerRepository(Hane24Apis.hane24ApiService, App.sharedPreferenceUtils),
            TimeDBRepository(createDatabase())
        )
    }
    private val reissueViewModel: ReissueViewModel by viewModels {
        ReissueViewModelFactory(
            ReissueRepository(
                Hane24Apis.hane24ApiService,
                App.sharedPreferenceUtils
            )
        )
    }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            overViewViewModel::refresh,
            logViewModel::reloadLogs,
            reissueViewModel::reload
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Hane24(
                overViewModel = overViewViewModel,
                logViewModel = logViewModel,
                mainViewModel = mainViewModel,
                reissueViewModel = reissueViewModel
            )
        }
        setStatusAndNavigationBar()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.errorHandler.collect {
                    it?.run { handle(this@MainActivity) }
                }
            }
        }
    }


    private fun setStatusAndNavigationBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        val currentNightMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
        controller.isAppearanceLightStatusBars = !currentNightMode
        controller.isAppearanceLightNavigationBars = !currentNightMode
    }

    private fun setViewModelObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                overViewViewModel.mainInfo.collect {
                    logViewModel.updateInOutState(it.inoutState == "IN", it.tagAt)
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Hane24(
    overViewModel: OverViewViewModel,
    logViewModel: LogViewModel,
    mainViewModel: MainViewModel,
    reissueViewModel: ReissueViewModel
) {
    val refreshScope = rememberCoroutineScope()

    fun refresh() = refreshScope.launch {
        mainViewModel.refresh()
    }

    val navController = rememberNavController()
    val mainLoading by mainViewModel.loading.collectAsState()
    val state = rememberPullRefreshState(mainLoading, ::refresh)
    val inOut by overViewModel.inOutState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.overview_in_color))
    ) {
        if (inOut && navBackStackEntry?.destination?.route == Navigation.Overview.route) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.in_background),
                contentDescription = "background",
                contentScale = ContentScale.FillHeight
            )
        }
        Scaffold(
            bottomBar = {
                BottomNav(
                    navController
                ) {
                    logViewModel.updateLogs(
                        TodayCalendarUtils.year,
                        TodayCalendarUtils.month,
                        TodayCalendarUtils.day
                    )
                }
            },
            backgroundColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .pullRefresh(state)
            ) {
                NavigationGraph(
                    navController = navController,
                    overViewViewModel = overViewModel,
                    logViewModel = logViewModel,
                    reissueViewModel = reissueViewModel
                )
                PullRefreshIndicator(
                    refreshing = mainLoading,
                    state = state,
                    modifier = Modifier.align(Alignment.TopCenter),
                )

            }
        }
    }
}
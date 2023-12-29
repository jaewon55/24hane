package com.hane24.hoursarenotenough24

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.hane24.hoursarenotenough24.database.createDatabase
import com.hane24.hoursarenotenough24.databinding.ActivityMainBinding
import com.hane24.hoursarenotenough24.inoutlog.LogViewModel
import com.hane24.hoursarenotenough24.inoutlog.LogViewModelFactory
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.overview.OverViewModelFactory
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.reissue.ReissueViewModelFactory
import com.hane24.hoursarenotenough24.reissue.ReissueViewModelNew
import com.hane24.hoursarenotenough24.repository.ReissueRepository
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository
import com.hane24.hoursarenotenough24.repository.UserRepository
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater).apply { lifecycleOwner = this@MainActivity }
    }
    private val overViewViewModel: OverViewViewModel by viewModels {
        OverViewModelFactory(
            App.sharedPreferenceUtilss,
            UserRepository(Hane24Apis.hane24ApiService, App.sharedPreferenceUtilss)
        )
    }

    private val logViewModel: LogViewModel by viewModels {
        LogViewModelFactory(
            TimeServerRepository(Hane24Apis.hane24ApiService, App.sharedPreferenceUtilss),
            TimeDBRepository(createDatabase())
        )
    }
    private val reissueViewModel: ReissueViewModelNew by viewModels {
        ReissueViewModelFactory(
            ReissueRepository(
                Hane24Apis.hane24ApiService,
                App.sharedPreferenceUtilss
            )
        )
    }
    private val mainViewModel: MainViewModel by viewModels { MainViewModelFactory(overViewViewModel::refresh) }

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

    fun moveToFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }


    private fun setStatusAndNavigationBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        val currentNightMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
        controller.isAppearanceLightStatusBars = !currentNightMode
        controller.isAppearanceLightNavigationBars = !currentNightMode
        if (currentNightMode) {
            binding.loadingLayout.background =
                AppCompatResources.getDrawable(this, R.color.default_text)
            binding.loadingProgressbar.setImageResource(R.drawable.loading_dark_animated_vector)
        } else {
            binding.loadingProgressbar.setImageResource(R.drawable.loading_animated_vector)
        }
    }

//    private fun setRefresh() {
//        binding.swipeRefreshLayout.isRefreshing
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            mainViewModel.refresh()
//            reissueViewModel.reload()
//        }
//    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Hane24(
    overViewModel: OverViewViewModel,
    logViewModel: LogViewModel,
    mainViewModel: MainViewModel,
    reissueViewModel: ReissueViewModelNew
) {
    val refreshScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val scrollState = rememberScrollState()
    var refreshing by remember { mutableStateOf(false) }
//    fun refresh() = refreshScope.launch {
//        refreshing = true
//        mainViewModel.refresh()
//        reissueViewModel.reload()
//        refreshing = false
//    }

//    val state = rememberPullRefreshState(refreshing, ::refresh)

    Scaffold(bottomBar = { BottomNav(navController) }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            NavigationGraph(
                navController = navController,
                overViewViewModel = overViewModel,
                logViewModel = logViewModel
            )
        }
    }
}
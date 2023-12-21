package com.hane24.hoursarenotenough24

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.drawable.AnimatedVectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hane24.hoursarenotenough24.database.createDatabase
import com.hane24.hoursarenotenough24.databinding.ActivityMainBinding
import com.hane24.hoursarenotenough24.etcoption.EtcOptionFragment
import com.hane24.hoursarenotenough24.inoutlog.LogListFragment
import com.hane24.hoursarenotenough24.inoutlog.LogListViewModel
import com.hane24.hoursarenotenough24.inoutlog.LogViewModel
import com.hane24.hoursarenotenough24.inoutlog.LogViewModelFactory
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.overview.OverViewFragment
import com.hane24.hoursarenotenough24.overview.OverViewModelFactory
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.reissue.ReissueViewModel
import com.hane24.hoursarenotenough24.reissue.ReissueViewModelFactory
import com.hane24.hoursarenotenough24.reissue.ReissueViewModelNew
import com.hane24.hoursarenotenough24.repository.ReissueRepository
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository
import com.hane24.hoursarenotenough24.repository.UserRepository
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
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

    //    private val logListViewModel: LogListViewModel by viewModels ()
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
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        setContentView(binding.root)
//        refreshWidget()
        setStatusAndNavigationBar()
        setFragmentsViewModel()
        setRefresh()
        setBottomNavigation()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.errorHandler.collect {
                    it?.run { handle(this@MainActivity) }
                }
            }
        }
    }

    private fun setBottomNavigation() {
        val animationDrawable = binding.loadingProgressbar.drawable as AnimatedVectorDrawable
        animationDrawable.start()
        binding.bottomNavigation.setOnItemSelectedListener {
            if (it.isChecked) {
                true
            } else {
                val fragment = when (it.itemId) {
                    R.id.bottom_navigation_home_menu -> {
                        OverViewFragment()
                    }

                    R.id.bottom_navigation_calendar_menu -> {
                        calendarBackToToday()
                        LogListFragment()
                    }

                    else -> {
                        EtcOptionFragment()
                    }
                }
                moveToFragment(fragment)
                true
            }
        }
    }

    private fun calendarBackToToday() {
        logViewModel.updateLogs(
            TodayCalendarUtils.year,
            TodayCalendarUtils.month,
            TodayCalendarUtils.day
        )
//        logListViewModel.changeCalendarDate(
//            TodayCalendarUtils.year,
//            TodayCalendarUtils.month,
//            TodayCalendarUtils.day
//        )
    }

    fun moveToFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    override fun onStop() {
        super.onStop()
//        this.sendBroadcast(Intent(this, BasicWidget::class.java).apply {
//            this.action = "ANIM_OFF"
//        })
    }

//    private fun refreshWidget() = this.sendBroadcast(Intent(this, BasicWidget::class.java).apply {
//        this.action = "REFRESH"
//    })

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

    private fun setFragmentsViewModel() {
        binding.overViewViewModel = overViewViewModel
        reissueViewModel.reissueState
    }

    private fun setRefresh() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                overViewViewModel.refreshLoading.collect {
//                    if (!it
//                        && logListViewModel.loadingState.value == false
//                        && reissueViewModel.loadingState.value == false
//                        && binding.swipeRefreshLayout.isRefreshing
//                    ) {
//                        binding.swipeRefreshLayout.isRefreshing = false
//                    }
//                }
//            }
//        }
//        logListViewModel.loadingState.observe(this) {
//            if (!it
//                && overViewViewModel.refreshLoading.value == false
//                && reissueViewModel.loadingState.value == false
//                && binding.swipeRefreshLayout.isRefreshing
//            ) {
//                binding.swipeRefreshLayout.isRefreshing = false
//            }
//        }
//        reissueViewModel.loadingState.observe(this) {
//            if (!it
//                && logListViewModel.loadingState.value == false
//                && overViewViewModel.refreshLoading.value == false
//                && binding.swipeRefreshLayout.isRefreshing
//            ) {
//                binding.swipeRefreshLayout.isRefreshing = false
//            }
//        }
        binding.swipeRefreshLayout.isRefreshing
        binding.swipeRefreshLayout.setOnRefreshListener {
            mainViewModel.refresh()
            reissueViewModel.reload()
//            overViewViewModel.refreshButtonOnClick()
//            logListViewModel.refreshButtonOnClick()
//            reissueViewModel.refreshButtonOnClick()
        }
    }

//    private fun logOutOnClick() {
//        SharedPreferenceUtils.saveAccessToken("")
//
//        val intent = Intent(this, LoginActivity::class.java)
//            .putExtra("loginState", State.LOGIN_FAIL)
//            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(intent).also { finish() }
//    }
//
//    private fun licenseOnClick() {
//        val dialog = LicenseDialogFragment()
//        supportFragmentManager.let {
//            dialog.show(it, "OpenSourceLicenses")
//        }
//    }
}
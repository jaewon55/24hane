package com.hane24.hoursarenotenough24

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hane24.hoursarenotenough24.databinding.ActivityMainBinding
import com.hane24.hoursarenotenough24.error.NetworkErrorDialog
import com.hane24.hoursarenotenough24.error.NetworkObserverImpl
import com.hane24.hoursarenotenough24.etcoption.EtcOptionFragment
import com.hane24.hoursarenotenough24.inoutlog.LogListFragment
import com.hane24.hoursarenotenough24.inoutlog.LogListViewModel
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.overview.OverViewFragment
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.widget.BasicWidget

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater).apply { lifecycleOwner = this@MainActivity }
    }
    private val overViewViewModel: OverViewViewModel by viewModels()
    private val logListViewModel: LogListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        refreshWidget()
        setStatusAndNavigationBar()
        setFragmentsViewModel()
        OverViewFragment()
        binding.bottomNavigation.setOnItemSelectedListener {
            val fragment = when(it.itemId) {
                R.id.bottom_navigation_home_menu -> OverViewFragment()
                R.id.bottom_navigation_calendar_menu -> LogListFragment()
                else -> EtcOptionFragment()
            }
            moveToFragment(fragment)
            true
        }
    }

    private fun moveToFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    override fun onStop() {
        super.onStop()
        this.sendBroadcast(Intent(this, BasicWidget::class.java).apply {
            this.action = "ANIM_OFF"
        })
    }

    private fun refreshWidget() = this.sendBroadcast(Intent(this, BasicWidget::class.java).apply {
        this.action = "REFRESH"
    })

    private fun setStatusAndNavigationBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }
    private fun setFragmentsViewModel() {
        binding.overViewViewModel = overViewViewModel
    }


//
//    private fun licenseOnClick() {
//        val dialog = LicenseDialogFragment()
//        supportFragmentManager.let {
//            dialog.show(it, "OpenSourceLicenses")
//        }
//    }
}
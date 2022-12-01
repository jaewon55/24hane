package com.hane24.hoursarenotenough24

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.hane24.hoursarenotenough24.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val pager by lazy { binding.contentMain.viewpager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setStatusAndNavigationBar()
        setToolbar()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerVisible(binding.navView)) {
            binding.drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    private fun setStatusAndNavigationBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }

    private fun setToolbar() {
        setSupportActionBar(binding.appBar.toolbar)
        setDrawerLayout()
        setNavigationItemListener()
    }

    private fun setDrawerLayout() {
        val drawerLayout = binding.drawerLayout

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.appBar.menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
            }

            override fun onDrawerClosed(drawerView: View) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    private fun setNavigationItemListener() {
        binding.navView.setNavigationItemSelectedListener { item ->
            var browserIntent: Intent? = when (item.itemId) {
                R.id.nav_item_android -> Intent(Intent.ACTION_VIEW, Uri.parse(PAGE_GUID))
                R.id.nav_item_ios -> Intent(Intent.ACTION_VIEW, Uri.parse(PAGE_GUID))
                R.id.nav_item_page_guide -> Intent(Intent.ACTION_VIEW, Uri.parse(PAGE_GUID))
                R.id.nav_item_inquire -> Intent(Intent.ACTION_VIEW, Uri.parse(INQUIRE_PAGE))
                else -> null
            }
            browserIntent?.let {
                startActivity(browserIntent)
            }
            true
        }
        binding.navFooterView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_item_logout -> deleteToken()
                R.id.nav_item_license -> licenseFunc()
            }
            true
        }
    }

    private fun deleteToken() {}

    private fun licenseFunc() {}

    companion object {
        private const val PAGE_GUID =
            "https://spot-tomato-468.notion.site/2022-42-SEOUL-bf0513c7197f4f71b4be968b8a2cd75a"
        private const val INQUIRE_PAGE = "https://forms.gle/SfSoaTcmyiPdMSCp9"
        private const val NUM_PAGES = 2
    }

//    private inner class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
//        override fun getItemCount(): Int = NUM_PAGES
//
//        override fun createFragment(position: Int): Fragment {
//            return when (position) {
//                0 -> MainFragment()
//                else -> MonthRecordFragment()
//            }
//        }
//    }

}
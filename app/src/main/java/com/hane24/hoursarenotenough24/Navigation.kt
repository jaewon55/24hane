package com.hane24.hoursarenotenough24

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hane24.hoursarenotenough24.etcoption.EtcOptionScreen
import com.hane24.hoursarenotenough24.inoutlog.LogCalendarScreen
import com.hane24.hoursarenotenough24.inoutlog.LogViewModel
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.overview.OverviewScreen
import com.hane24.hoursarenotenough24.reissue.ReissueFragment

sealed class Navigation(val route: String, @DrawableRes val defaultIcon: Int, @DrawableRes val selectedIcon: Int) {
    object Overview : Navigation("overview", R.drawable.ic_home, R.drawable.ic_home_selected)
    object LogCalendar : Navigation("log_calendar", R.drawable.ic_calendar, R.drawable.ic_calendar_selected)
    object Option : Navigation("option", R.drawable.ic_menu, R.drawable.ic_menu_selected)
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    overViewViewModel: OverViewViewModel,
    logViewModel: LogViewModel,
) {
    val context = LocalContext.current as FragmentActivity
    fun reissueOnClick() {
        context.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, ReissueFragment())
            .commit()
    }

    fun logOutOnClick() {
        App.sharedPreferenceUtilss.saveAccessToken("")
        logViewModel.deleteAllLogsInDatabase()

        val intent = Intent(context, LoginActivity::class.java)
            .putExtra("loginState", State.LOGIN_FAIL)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent).also { context.finish() }
    }

    NavHost(navController = navController, startDestination = Navigation.Overview.route) {
        composable(Navigation.Overview.route) {
            OverviewScreen(viewModel = overViewViewModel)
        }
        composable(Navigation.LogCalendar.route) {
            LogCalendarScreen(viewModel = logViewModel)
        }
        composable(Navigation.Option.route) {
            EtcOptionScreen(logoutOnClick = ::logOutOnClick, reissueOnClick = ::reissueOnClick)
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf<Navigation>(
        Navigation.Overview,
        Navigation.LogCalendar,
        Navigation.Option
    )

    val onClick = { item: Navigation ->
        navController.navigate(item.route) {
            navController.graph.startDestinationRoute?.let {
                popUpTo(it) { saveState = true }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BottomNavigation(backgroundColor = Color.White) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = item.route == currentRoute,
                onClick = { onClick(item) },
                icon = {
                    if (item.route != currentRoute) {
                        Icon(
                            painter = painterResource(item.defaultIcon),
                            contentDescription = "${item.route} btn"
                        )
                    } else {
                        Icon(painter = painterResource(item.selectedIcon), contentDescription = "${item.route} btn")
                    }

                })
        }
    }
}

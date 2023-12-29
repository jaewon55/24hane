package com.hane24.hoursarenotenough24.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.*
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.BuildConfig
import com.hane24.hoursarenotenough24.MainActivity
import com.hane24.hoursarenotenough24.R
import kotlinx.coroutines.delay

class LoginActivity : ComponentActivity() {
    private var backgroundColor: Color = Color.White
    private val viewModel: LoginViewModel by viewModels { LoginViewModelFactory(App.sharedPreferenceUtils) }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentNightMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        backgroundColor = if (currentNightMode)
            Color(ActivityCompat.getColor(this, R.color.default_text))
        else
            Color.White

        setStatusAndNavigationBar(currentNightMode)

        setContent {
            val modifier = Modifier.background(backgroundColor)

            Scaffold(modifier = modifier) { _ ->
                LoginPage(isNightMode = currentNightMode)
            }
        }
    }

    private fun setStatusAndNavigationBar(currentNightMode: Boolean) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = !currentNightMode
        controller.isAppearanceLightNavigationBars = !currentNightMode
    }

    @Composable
    @Preview
    fun LoginPage(modifier: Modifier = Modifier, isNightMode: Boolean = true) {
        val loading = rememberSaveable { mutableStateOf(true) }

        Box(modifier = Modifier.background(backgroundColor)) {
            LoginWebView(loading = loading)
            if (loading.value) {
                LoginLoadingComponent(
                    isNightMode = isNightMode
                )
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    @Preview
    fun LoginWebView(
        modifier: Modifier = Modifier,
        loading: MutableState<Boolean> = mutableStateOf(true),
        ) {
        val isLogin = viewModel.state.collectAsStateWithLifecycle()

        if (isLogin.value) {
            startActivity(Intent(LocalContext.current, MainActivity::class.java)
                .apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
        }

        val cookieManager = CookieManager.getInstance()
        val webViewClient = remember { CustomWebViewClient(loading, cookieManager, this) }
        val uri = Uri.Builder().scheme("https").authority(BuildConfig.LOGIN_URL)
            .appendPath("user")
            .appendPath("login")
            .appendPath("42")
            .appendQueryParameter("redirect", "app://hane42")
            .build()

        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                settings.javaScriptEnabled = true
                this.webViewClient = webViewClient
                loadUrl(uri.toString())
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)
            }
        }, modifier = modifier.background(backgroundColor))
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
@Preview
fun LoginLoadingComponent(
    modifier: Modifier = Modifier,
    isNightMode: Boolean = false
) {
    val resId = if (isNightMode)
        R.drawable.loading_dark_animated_vector
    else
        R.drawable.loading_animated_vector

    val backgroundColor = if (isNightMode)
        Color(ActivityCompat.getColor(LocalContext.current, R.color.default_text))
    else
        Color.White

    val animateImage = AnimatedImageVector.animatedVectorResource(resId)
    var atEnd by remember { mutableStateOf(false) }
    val duration = 1500L

    Column(
        modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAnimatedVectorPainter(animatedImageVector = animateImage, atEnd = atEnd),
            contentDescription = "loading_icon",
            contentScale = ContentScale.Fit
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(duration)
            atEnd = !atEnd
        }
    }
}

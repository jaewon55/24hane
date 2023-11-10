package com.hane24.hoursarenotenough24.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.*
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hane24.hoursarenotenough24.BuildConfig
import com.hane24.hoursarenotenough24.MainActivity
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.delay

class LoginActivity : ComponentActivity() {
    private lateinit var cookieManager: CookieManager
    private var backgroundColor: Color = Color.White

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

    private fun createLoginUri() =
        Uri.Builder().scheme("https").authority(BuildConfig.LOGIN_URL)
            .appendPath("user")
            .appendPath("login")
            .appendPath("42")
            .appendQueryParameter("redirect", "app://hane42")
            .build()

    @Composable
    @Preview
    fun LoginPage(modifier: Modifier = Modifier, isNightMode: Boolean = true) {

        val loading = rememberSaveable { mutableStateOf(true) }

        Box(modifier = Modifier.background(backgroundColor)) {
            LoginWebView(loading = loading)
            if (loading.value) {
                LoadingComponent()
            }
        }
    }

    @OptIn(ExperimentalAnimationGraphicsApi::class)
    @Composable
    @Preview
    fun LoadingComponent(
        modifier: Modifier = Modifier,
        isNightMode: Boolean = true
        ) {
        val resId = if (isNightMode)
                R.drawable.loading_dark_animated_vector
            else
                R.drawable.loading_animated_vector
        val animateImage = AnimatedImageVector.animatedVectorResource(resId)
        var atEnd by remember { mutableStateOf(false) }
        val duration = 1500L

        Column(
            modifier
                .fillMaxSize().background(backgroundColor),
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

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    @Preview
    fun LoginWebView(
        modifier: Modifier = Modifier,
        loading: MutableState<Boolean> = mutableStateOf(true),
        ) {
        cookieManager = CookieManager.getInstance()
        val webViewClient = remember { CustomWebViewClient(loading) }

        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                settings.javaScriptEnabled = true
                this.webViewClient = webViewClient
                loadUrl(createLoginUri().toString())
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)
            }
        }, modifier = modifier.background(backgroundColor))
    }

    inner class CustomWebViewClient(private val loading: MutableState<Boolean>): WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return redirect(request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loading.value = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            loading.value = false
        }

        private fun parseAccessToken(): String =
            cookieManager.getCookie("https://api.24hoursarenotenough.42seoul.kr/user/login/callback/42")
                .substringAfter(' ').split('=')[1]
        private fun redirectToMain() {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent).also {
                val accessToken = parseAccessToken()
                SharedPreferenceUtils.saveAccessToken(accessToken)
                cookieManager.removeAllCookies {}
                this@LoginActivity.finish()
            }
        }

        private fun redirect(request: WebResourceRequest?): Boolean {
            if (request?.url.toString() == "app://hane42" || request?.url.toString() == "https://profile.intra.42.fr/") {
                return try {
                    redirectToMain()
                    true
                } catch (e: java.lang.Exception) {
                    false
                }
            }
            return false
        }
    }
}


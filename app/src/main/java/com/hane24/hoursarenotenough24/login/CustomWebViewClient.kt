package com.hane24.hoursarenotenough24.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.MutableState
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.MainActivity

class CustomWebViewClient(private val loading: MutableState<Boolean>, private val cookieManager: CookieManager, private val context: Activity): WebViewClient() {
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
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent).also {
            val accessToken = parseAccessToken()
            App.sharedPreferenceUtils.saveAccessToken(accessToken)
            cookieManager.removeAllCookies {}
            context.finish()
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
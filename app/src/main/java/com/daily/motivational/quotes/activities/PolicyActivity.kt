package com.daily.motivational.quotes.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {
    var policyBinding: ActivityPolicyBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        policyBinding =
            DataBindingUtil.setContentView(this@PolicyActivity, R.layout.activity_policy)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        policyBinding?.mWebView?.webViewClient = MyWebViewClient()
        openURL()
        policyBinding?.mBack?.setOnClickListener(View.OnClickListener { onBackPressed() })
    }

    private fun openURL() {
        policyBinding?.mWebView?.loadUrl("https://sites.google.com/view/imagepdfprivacypolicy/home")
        policyBinding?.mWebView?.requestFocus()
    }

    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}
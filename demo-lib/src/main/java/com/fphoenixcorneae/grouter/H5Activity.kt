package com.fphoenixcorneae.grouter

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.grouter.annotation.Router
import com.fphoenixcorneae.grouter.lib.R


@Router(scheme = "https", path = "/h5")
class H5Activity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h5)
        intent.getStringExtra("h5url")?.let {
            findViewById<WebView>(R.id.wvWebpage).apply {
                settings.javaScriptEnabled = true // 如果需要支持JavaScript
                webViewClient = WebViewClient() // 处理页面跳转等
                loadUrl(it)
            }
        }
    }
}
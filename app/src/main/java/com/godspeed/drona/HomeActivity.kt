package com.godspeed.drona

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.godspeed.drona.utils.SharedPrefManager
import com.godspeed.drona.utils.SharedPrefManager.SCREEN_NAME


class HomeActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    val welcomeUrl="https://drona.digitalninza.com/drona_backend_7/complete"
    val signUpScreenUrl="https://drona.digitalninza.com/drona_backend_7/signup"
    val resetScreenUrl="https://drona.digitalninza.com/drona_backend_7/reset"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val signUpScren  = intent!!.getStringExtra(SCREEN_NAME)
        webView =  findViewById(R.id.webView)
        progressBar =  findViewById(R.id.progressBar)
        webView.webViewClient = WebViewClient()
//        https://drona.digitalninza.com/drona_backend_7/Front/Front_api/proceed_api_url/eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb2NhbGhvc3QiLCJpYXQiOjE2MjcwNDUzNjQsIm5iZiI6MTYyNzA0NTM2NCwiZXhwIjoxNjI3MTMxNzY0LCJhdWQiOiJ1c2VycyIsImRhdGEiOnsidV91dWlkIjoiNTc3MTZhYmMtZDYzZS0xMWViLWJkMGMtZmExNjNlMjA5ZjliIiwiY3V1aWQiOiI2NjQ3NjkyYi1kNjNmLTExZWItYmQwYy1mYTE2M2UyMDlmOWIiLCJydXVpZCI6ImQ0ZmU2NDJlLTQ1Y2YtMTFlYi04NDQxLWZhMTYzZTIwOWY5YiJ9fQ.H69qxy03RubqteMn2s5JN1bKQJsPlQj4exwiOZqdWy0/57716abc-d63e-11eb-bd0c-fa163e209f9b/4526ddc7-ebb6-11eb-8f96-fa163e209f9b
//        https://drona.digitalninza.com/drona_backend_7/login
        val token: String = SharedPrefManager.read(SharedPrefManager.WEB_TOKEN, "")
        val user_id: String = SharedPrefManager.read(SharedPrefManager.USER_ID, "")
        val token_id: String = SharedPrefManager.read(SharedPrefManager.WEB_TOKEN_ID, "")
        var finalUrl =    "https://drona.digitalninza.com/drona_backend_7/Front/Front_api/proceed_api_url/"+token+"/"+user_id+"/"+token_id
        if(signUpScren.contentEquals(SharedPrefManager.SIGN_UP_SCREEN)){
            finalUrl = signUpScreenUrl
        }else if(signUpScren.contentEquals(SharedPrefManager.FORGOT_SCREEN)){
            finalUrl = resetScreenUrl
        }

        try {
            val settings: WebSettings = webView.getSettings()
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)
            settings.builtInZoomControls = true
            settings.javaScriptEnabled = true
//            settings.setAppCacheEnabled(true)
//            settings.setAppCacheMaxSize((10 * 1024 * 1024).toLong())
//            settings.setAppCachePath("")
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.setGeolocationEnabled(true)
            settings.saveFormData = false
            settings.savePassword = false
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            // Flash settings
            settings.pluginState = WebSettings.PluginState.ON

            // Geo location settings
            settings.setGeolocationEnabled(true)
            settings.setGeolocationDatabasePath("/data/data/selendroid")
        } catch (e: Exception) {
//            SelendroidLogger.error("Error configuring web view", e)
        }
        webView.loadUrl( finalUrl)
    }

    inner class WebViewClient : android.webkit.WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if(url.contains(welcomeUrl)){
                val i = Intent(applicationContext, LoginActivity::class.java)
                startActivity(i)
                finish()
            }
            view.loadUrl(url)
            return false
        }
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }
    }
}
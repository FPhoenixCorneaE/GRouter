package com.fphoenixcorneae.grouter

import android.app.Application
import com.fphoenixcorneae.grouter.call.GRouterClient

class DemoApp : Application() {

    companion object {
        var isSignedIn = true
        private lateinit var sInstance: DemoApp

        fun getInstance() = sInstance
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        GRouter.setClient(
            GRouterClient.Builder()
                .addInterceptor(LoginInterceptor())
                .addInterceptor(H5UrlInterceptor())
                .build()
        )
    }
}
package com.fphoenixcorneae.grouter

import android.content.Intent
import android.net.Uri
import com.fphoenixcorneae.grouter.call.GRouterResponse
import com.fphoenixcorneae.grouter.interceptor.Interceptor

class LoginInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): GRouterResponse {
        val originalRequest = chain.request()
        if (!DemoApp.isSignedIn) {
            GRouter.findTargetByUri(Uri.parse("android://com.google.router/login"))?.let { route ->
                val intent = Intent(originalRequest.requireContext(), route.target.java)
                return GRouterResponse.Builder().request(
                    originalRequest.newBuilder().extras(intent).build()
                ).build()
            }
        }
        return chain.proceed(originalRequest)
    }
}
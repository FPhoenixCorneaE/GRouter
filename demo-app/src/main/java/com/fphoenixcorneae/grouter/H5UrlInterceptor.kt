package com.fphoenixcorneae.grouter

import android.content.Intent
import android.net.Uri
import com.fphoenixcorneae.grouter.call.GRouterResponse
import com.fphoenixcorneae.grouter.interceptor.Interceptor

class H5UrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): GRouterResponse {
        val originalRequest = chain.request()
        val uri = originalRequest.uri()
        if (uri?.scheme?.startsWith("http") == true) {
            GRouter.findTargetByUri(Uri.parse("https://com.google.router/h5"))?.let { route ->
                val intent = Intent(originalRequest.requireContext(), route.target.java).apply {
                    putExtra("h5url", uri.toString())
                }
                return GRouterResponse.Builder().request(
                    originalRequest.newBuilder().extras(intent).build()
                ).build()
            }
        }
        return chain.proceed(originalRequest)
    }
}
package com.fphoenixcorneae.grouter.interceptor

import android.content.Intent
import androidx.fragment.app.Fragment
import com.fphoenixcorneae.grouter.GRouter
import com.fphoenixcorneae.grouter.call.GRouterResponse

/**
 * Uri 拦截器
 */
class UriInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): GRouterResponse {
        val originalRequest = chain.request()
        val uri = originalRequest.uri()
        uri?.let {
            GRouter.findTargetByUri(it)?.let { route ->
                val extras = Intent()
                for (key in uri.queryParameterNames) {
                    extras.putExtra(key, uri.getQueryParameter(key))
                }
                val intent =
                    if (Fragment::class.java.isAssignableFrom(route.target.java) && originalRequest.fragmentContainerProvider() != null) {
                        originalRequest.fragmentContainerProvider()!!.invoke().apply {
                            putExtras(extras)
                        }
                    } else {
                        Intent(originalRequest.requireContext(), route.target.java).apply {
                            putExtras(extras)
                        }
                    }
                originalRequest.extras()?.let { intent.putExtras(it) }
                return chain.proceed(originalRequest.newBuilder().extras(intent).build())
            }
        }
        return chain.proceed(originalRequest)
    }
}

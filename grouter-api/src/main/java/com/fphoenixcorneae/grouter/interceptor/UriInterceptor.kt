package com.fphoenixcorneae.grouter.interceptor

import android.content.Intent
import androidx.fragment.app.Fragment
import com.fphoenixcorneae.grouter.GRouter
import com.fphoenixcorneae.grouter.call.GRouterResponse

/**
 * URI 拦截器
 */
class UriInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): GRouterResponse {
        val request = chain.request()
        val uri = request.uri()
        uri?.let {
            GRouter.findTargetByUri(it)?.let { route ->
                val extras = Intent()
                for (key in uri.queryParameterNames) {
                    extras.putExtra(key, uri.getQueryParameter(key))
                }
                val intent =
                    if (Fragment::class.java.isAssignableFrom(route.target.java) && request.fragmentContainerProvider() != null) {
                        request.fragmentContainerProvider()!!.invoke().apply {
                            putExtras(extras)
                        }
                    } else {
                        Intent(request.requireContext(), route.target.java).apply {
                            putExtras(extras)
                        }
                    }
                request.extras()?.let { intent.putExtras(it) }
                return GRouterResponse.Builder()
                    .request(request.newBuilder().extras(intent).build())
                    .build()
            }
        }
        return chain.proceed(request)
    }
}

package com.fphoenixcorneae.grouter.call

import com.fphoenixcorneae.grouter.interceptor.GRouterInterceptorChain
import com.fphoenixcorneae.grouter.interceptor.Interceptor
import com.fphoenixcorneae.grouter.interceptor.UriInterceptor

class GRouterCall private constructor(
    private val client: GRouterClient,
    private val originalRequest: GRouterRequest,
) : Call {
    companion object {
        fun newCall(client: GRouterClient, originalRequest: GRouterRequest): GRouterCall {
            return GRouterCall(client, originalRequest)
        }
    }

    private var executed = false

    override fun interceptors(): List<Interceptor> {
        return client.interceptors().plus(UriInterceptor())
    }

    override fun originalRequest(): GRouterRequest {
        return originalRequest
    }

    override fun execute(): GRouterResponse {
        if (executed) {
            throw IllegalStateException("Router call is already executed.")
        }
        executed = true
        val interceptorChain = GRouterInterceptorChain(this)
        return interceptorChain.proceed(originalRequest)
    }

    override fun isExecuted(): Boolean {
        return executed
    }
}
package com.fphoenixcorneae.grouter.interceptor

import com.fphoenixcorneae.grouter.call.Call
import com.fphoenixcorneae.grouter.call.GRouterRequest
import com.fphoenixcorneae.grouter.call.GRouterResponse

class GRouterInterceptorChain internal constructor(
    private val call: Call,
    private val index: Int = 0
) : Interceptor.Chain {

    private var calls = 0

    override fun request(): GRouterRequest {
        return call.originalRequest()
    }

    override fun proceed(request: GRouterRequest): GRouterResponse {
        if (index >= call.interceptors().size) {
            return GRouterResponse.Builder().request(request).build()
        }
        calls++
        val next = GRouterInterceptorChain(call, index + 1)
        val interceptor = call.interceptors().getOrNull(index)
        val response = interceptor?.intercept(next)

        // Confirm that the next interceptor made its required call to chain.proceed().
        if (response == null && index + 1 < call.interceptors().size && next.calls != 1) {
            throw IllegalStateException(
                "Router interceptor " + interceptor
                        + " must call proceed() exactly once"
            )
        }

        // Confirm that the intercepted response isn't null.
        if (response == null) {
            throw NullPointerException("Router interceptor $interceptor returned null")
        }
        return response
    }
}
package com.fphoenixcorneae.grouter.interceptor

import com.fphoenixcorneae.grouter.call.GRouterRequest
import com.fphoenixcorneae.grouter.call.GRouterResponse

interface Interceptor {
    fun intercept(chain: Chain): GRouterResponse

    interface Chain {
        fun request(): GRouterRequest
        fun proceed(request: GRouterRequest): GRouterResponse
    }
}

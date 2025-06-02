package com.fphoenixcorneae.grouter.call

import com.fphoenixcorneae.grouter.interceptor.Interceptor

interface Call {
    fun interceptors(): List<Interceptor>

    fun originalRequest(): GRouterRequest

    fun execute(): GRouterResponse

    fun isExecuted(): Boolean

    interface Factory {
        fun newCall(originalRequest: GRouterRequest): Call
    }
}

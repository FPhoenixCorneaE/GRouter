package com.fphoenixcorneae.grouter.call

import com.fphoenixcorneae.grouter.interceptor.Interceptor

class GRouterClient private constructor(private val builder: Builder) : Call.Factory {

    fun interceptors(): List<Interceptor> {
        val interceptors = mutableListOf<Interceptor>()
        interceptors.addAll(builder.interceptors)
        return interceptors
    }

    fun baseUrl() = builder.baseUrl

    override fun newCall(originalRequest: GRouterRequest): Call {
        return GRouterCall.newCall(this, originalRequest)
    }

    class Builder {
        internal val interceptors by lazy { mutableListOf<Interceptor>() }
        internal var baseUrl: String? = null

        fun addInterceptor(interceptor: Interceptor) = apply {
            interceptors.add(interceptor)
        }

        fun baseUrl(baseUrl: String) = apply {
            this.baseUrl = baseUrl
        }

        fun build() = GRouterClient(this)
    }
}
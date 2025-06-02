package com.fphoenixcorneae.grouter.call

import android.content.Context
import android.content.Intent

class GRouterResponse private constructor(private val builder: Builder) {

    fun requireContext(): Context {
        return builder.request?.requireContext()
            ?: throw IllegalStateException("context is recycled")
    }

    fun intent(): Intent? {
        return builder.request?.extras()
    }

    class Builder {
        internal var request: GRouterRequest? = null

        fun request(request: GRouterRequest) = apply {
            this.request = request
        }

        fun build() = GRouterResponse(this)
    }
}
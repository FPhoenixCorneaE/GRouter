package com.fphoenixcorneae.grouter.call

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fphoenixcorneae.grouter.startup.GRouterInitializer
import com.fphoenixcorneae.grouter.startup.RouteResult
import java.lang.ref.WeakReference

class GRouterRequest private constructor(private val builder: Builder) {

    fun newBuilder(): Builder {
        return Builder(this)
    }

    fun requireContext(): Context {
        return builder.contextRef.get() ?: throw IllegalStateException("context is recycled")
    }

    fun uri(): Uri? {
        return builder.uri
    }

    fun extras(): Intent? {
        return builder.extras
    }

    fun fragmentContainerProvider(): (() -> Intent)? {
        return builder.fragmentContainerProvider
    }

    class Builder(context: Context) {

        internal val contextRef = WeakReference(context)
        internal var uri: Uri? = null
        internal var extras: Intent? = null
        internal var fragmentContainerProvider: (() -> Intent)? = null


        constructor(request: GRouterRequest) : this(request.requireContext()) {
            this.uri = request.uri()
            this.extras = request.extras()
            this.fragmentContainerProvider = request.fragmentContainerProvider()
        }

        fun url(url: String) = apply {
            this.uri = Uri.parse(url)
        }

        fun extras(extras: Intent) = apply {
            this.extras = extras
        }

        fun fragmentContainerProvider(provider: () -> Intent) = apply {
            this.fragmentContainerProvider = provider
        }

        fun start(onResult: ((RouteResult) -> Unit)? = null) {
            val request = build()
            val routerStarter = GRouterInitializer()
            routerStarter.start(request, onResult)
        }

        fun build() = GRouterRequest(this)
    }
}
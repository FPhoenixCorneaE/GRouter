package com.fphoenixcorneae.grouter.startup

import com.fphoenixcorneae.grouter.call.GRouterRequest

interface Initializer {
    fun start(
        request: GRouterRequest,
        onResult: ((RouteResult) -> Unit)?
    )
}
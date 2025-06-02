package com.fphoenixcorneae.grouter.startup

import com.fphoenixcorneae.grouter.GRouter
import com.fphoenixcorneae.grouter.call.GRouterRequest

class GRouterInitializer : Initializer {
    override fun start(request: GRouterRequest, onResult: ((RouteResult) -> Unit)?) {
        val client = GRouter.getClient()
        val call = client.newCall(request)
        val response = call.execute()
        response.intent()?.let {
            runCatching {
                response.requireContext().startActivity(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}
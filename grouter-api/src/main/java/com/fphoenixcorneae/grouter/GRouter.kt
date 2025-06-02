package com.fphoenixcorneae.grouter

import android.content.Context
import android.net.Uri
import android.util.Log
import com.fphoenixcorneae.grouter.annotation.RouteInfo
import com.fphoenixcorneae.grouter.call.GRouterClient
import com.fphoenixcorneae.grouter.call.GRouterRequest
import com.fphoenixcorneae.grouter.interceptor.UriInterceptor
import com.fphoenixcorneae.grouter.register.IRouteRegister

object GRouter {
    private val routes = mutableSetOf<RouteInfo>()
    private var client = GRouterClient.Builder().addInterceptor(UriInterceptor()).build()

    init {
        // 注册路由信息
        runCatching {
            val routeRegister =
                Class.forName("com.fphoenixcorneae.grouter.register.RouteRegister")
                    .getDeclaredConstructor()
                    .newInstance() as IRouteRegister
            routeRegister.registerRoutes()
        }.onFailure {
            Log.e(
                "GRouter",
                "RouteRegister.class not found, please add `grouter-plugin`"
            )
        }
    }

    fun with(context: Context): GRouterRequest.Builder {
        return GRouterRequest.Builder(context)
    }

    fun setClient(client: GRouterClient) {
        this.client = client
    }

    fun getClient() = client

    fun findTargetByUri(uri: Uri): RouteInfo? {
        return routes.find { route ->
            val routeUrl = route.url
            val targetUrl = if (!client.baseUrl().isNullOrEmpty()) {
                buildString {
                    append(client.baseUrl())
                    append(uri.path)
                }
            } else {
                buildString {
                    append(uri.scheme)
                    append("://")
                    append(uri.host)
                    append(uri.path)
                }
            }
            Regex(routeUrl).matches(targetUrl)
        }
    }

    fun register(route: RouteInfo) {
        routes.add(route)
    }
}
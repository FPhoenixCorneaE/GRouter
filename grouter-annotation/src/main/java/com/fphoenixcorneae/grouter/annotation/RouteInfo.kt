package com.fphoenixcorneae.grouter.annotation

import kotlin.reflect.KClass

/**
 * 真正的路由信息
 */
data class RouteInfo(
    val url: String,
    val target: KClass<*>,
    val description: String,
) {
    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is RouteInfo) {
            return url == other.url
        }
        return false
    }
}
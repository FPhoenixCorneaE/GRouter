package com.fphoenixcorneae.grouter.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Router(
    /**
     * 路由 scheme，不包含 "://"，例如 "http"，支持正则表达式，注意需要转义
     */
    val scheme: String = "",
    /**
     * 路由 host，不包含 "/"，例如 "www\\.baidu\\.com"，支持正则表达式，注意需要转义
     */
    val host: String = "",
    /**
     * 路由 path，必须以 "/" 开头，例如 "/index\\.html"，支持正则表达式，注意需要转义
     */
    val path: String = "",
    /**
     * 页面描述
     */
    val description: String = "",
)

fun Router.routeUrl(defaultScheme: String?, defaultHost: String?): String {
    val scheme = scheme.ifEmpty { defaultScheme }
    val host = host.ifEmpty { defaultHost }
    if (scheme.isNullOrEmpty() || scheme.contains(":") || scheme.contains("/")) {
        throw IllegalArgumentException("GRouter Scheme '$scheme' must not be null or empty and must not contains ':' or '/'")
    }
    if (host.isNullOrEmpty() || host.contains('/')) {
        throw IllegalArgumentException("GRouter Host '$host' must not be null or empty and must not contains '/'")
    }
    if (path.isEmpty() || !path.startsWith('/')) {
        throw IllegalArgumentException("GRouter Path '$path' must not be empty and must start with '/'")
    }
    return "$scheme://$host$path"
}
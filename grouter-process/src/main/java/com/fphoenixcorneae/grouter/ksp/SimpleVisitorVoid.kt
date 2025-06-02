package com.fphoenixcorneae.grouter.ksp

import com.fphoenixcorneae.grouter.annotation.RouteInfo
import com.fphoenixcorneae.grouter.annotation.Router
import com.fphoenixcorneae.grouter.annotation.routeUrl
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName

/**
 * KSVisitor 接口，该接口有两种泛型类型：
 * D：它是作为输入发送或在流程步骤中创建的对象。
 * R：这是我们的类将返回的对象。
 *
 * 这里使用了 KSVisitorVoid，因为我们的输入和输出都是空的。
 * 由于我们的注解目标是类，我们必须重写 visitClassDeclaration
 */
class SimpleVisitorVoid(
    private val symbol: KSClassDeclaration,
    private val funSpecBuilder: FunSpec.Builder,
    private val defaultScheme: String,
    private val defaultHost: String,
) : KSVisitorVoid() {

    private val supportTypes = setOf(
        "android.app.Activity",
        "android.app.Fragment",
        "androidx.fragment.app.Fragment"
    )

    /**
     * 注解目标是类，必须重写 visitClassDeclaration
     */
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        // 检查对象
        checkNotNull(
            symbol
                .getAllSuperTypes()
                .find {
                    it.declaration.toClassName().canonicalName in supportTypes
                }
        ) {
            String.format(
                Constant.CHECK_OBJECT_TYPE_EXCEPTION_MESSAGE,
                symbol.toClassName().canonicalName
            )
        }
        val routeInfo = RouteInfo::class.asClassName()
        val route = (symbol as KSDeclaration).getAnnotationsByType(Router::class).first()
        /**
         * Statement: GRouter.register(RouteInfo(url, target, description))
         */
        funSpecBuilder.addStatement(
            Constant.STATEMENT_PARAM_FORMAT,
            routeInfo,
            route.routeUrl(defaultScheme, defaultHost).toTranslateString(),
            classDeclaration.toClassName(),
            route.description.toTranslateString()
        )
    }
}
package com.fphoenixcorneae.grouter.ksp

import com.fphoenixcorneae.grouter.annotation.Router
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.jvm.jvmName

class SimpleSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val moduleName = options[Constant.KSP_ARGUMENT_KEY_MODULE_NAME].orEmpty()
        val defaultScheme = options[Constant.KSP_ARGUMENT_KEY_DEFAULT_SCHEME].orEmpty()
        val defaultHost = options[Constant.KSP_ARGUMENT_KEY_DEFAULT_HOST].orEmpty()

        require(moduleName.isNotEmpty() && defaultScheme.isNotEmpty() && defaultHost.isNotEmpty()) {
            Constant.KSP_ARGUMENT_EXCEPTION_MESSAGE
        }
        // 获取带有 @Router 注解的对象
        val symbols = resolver.getSymbolsWithAnnotation(Router::class.java.name)
            .filterIsInstance<KSClassDeclaration>()
        if (symbols.iterator().hasNext().not()) return emptyList()
        /**
         * Method: fun register()
         */
        val loadRouteMethodBuilder = FunSpec.builder(Constant.ROUTER_METHOD_NAME)
            .addKdoc(Constant.ROUTER_METHOD_DOC)
        // 遍历 Sequence 处理每一个 KSClassDeclaration 对象
        symbols.forEach { symbol ->
            symbol.accept(
                SimpleVisitorVoid(
                    symbol = symbol,
                    funSpecBuilder = loadRouteMethodBuilder,
                    defaultScheme = defaultScheme,
                    defaultHost = defaultHost,
                ),
                Unit,
            )
        }
        // 创建我们自动生成的文件
        val generatedClassName = "${Constant.ROUTER_GENERATE_CLASS_NAME_PREFIX}$moduleName"
        val fileSpec = FileSpec.builder(Constant.ROUTER_GENERATE_PACKAGE_NAME, generatedClassName)
            .jvmName(generatedClassName)
            .addImport(Constant.ROUTER_PACKAGE_NAME, Constant.ROUTER_NAME)
            .addFunction(loadRouteMethodBuilder.build())
            .build()

        // 写入文件
        codeGenerator
            .createNewFile(Dependencies.ALL_FILES, fileSpec.packageName, fileSpec.name)
            .plusAssign(fileSpec.toString())
        return emptyList()
    }
}
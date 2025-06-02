package com.fphoenixcorneae.grouter.ksp

import com.google.devtools.ksp.ExceptionMessage
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.squareup.kotlinpoet.ClassName
import java.io.OutputStream
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

fun <T : Annotation> KSAnnotated.getAnnotationsByType(annotationKClass: KClass<T>): Sequence<T> {
    return this.annotations.filter {
        it.shortName.getShortName() == annotationKClass.simpleName && it.annotationType.resolve().declaration
            .qualifiedName?.asString() == annotationKClass.qualifiedName
    }.map { it.toAnnotation(annotationKClass.java) }
}

@Suppress("UNCHECKED_CAST")
private fun <T : Annotation> KSAnnotation.toAnnotation(annotationClass: Class<T>): T {
    return Proxy.newProxyInstance(
        annotationClass.classLoader,
        arrayOf(annotationClass),
        createInvocationHandler(annotationClass)
    ) as T
}

private fun KSAnnotation.createInvocationHandler(clazz: Class<*>): InvocationHandler {
    val cache = ConcurrentHashMap<Pair<Class<*>, Any>, Any>(arguments.size)
    return InvocationHandler { proxy, method, _ ->
        if (method.name == "toString" && arguments.none { it.name?.asString() == "toString" }) {
            clazz.canonicalName +
                    arguments.map { argument: KSValueArgument ->
                        // handles default values for enums otherwise returns null
                        val methodName = argument.name?.asString()
                        val value = proxy.javaClass.methods.find { m -> m.name == methodName }?.invoke(proxy)
                        "$methodName=$value"
                    }.toList()
        } else {
            val argument = arguments.first { it.name?.asString() == method.name }
            when (val result = argument.value ?: method.defaultValue) {
                is Proxy -> result
                is List<*> -> {
                    val value = { result.asArray(method, clazz) }
                    cache.getOrPut(Pair(method.returnType, result), value)
                }
                else -> {
                    when {
                        // Workaround for java annotation value array type
                        // https://github.com/google/ksp/issues/1329
                        method.returnType.isArray -> {
                            if (result !is Array<*>) {
                                val value = { result.asArray(method, clazz) }
                                cache.getOrPut(Pair(method.returnType, value), value)
                            } else {
                                throw IllegalStateException("unhandled value type, $ExceptionMessage")
                            }
                        }
                        method.returnType.isEnum -> {
                            val value = { result.asEnum(method.returnType) }
                            cache.getOrPut(Pair(method.returnType, result), value)
                        }
                        method.returnType.isAnnotation -> {
                            val value = { (result as KSAnnotation).asAnnotation(method.returnType) }
                            cache.getOrPut(Pair(method.returnType, result), value)
                        }
                        method.returnType.name == "java.lang.Class" -> {
                            cache.getOrPut(Pair(method.returnType, result)) {
                                when (result) {
                                    is KSType -> result.asClass(clazz)
                                    // Handles com.intellij.psi.impl.source.PsiImmediateClassType using reflection
                                    // since api doesn't contain a reference to this
                                    else -> Class.forName(
                                        result.javaClass.methods
                                            .first { it.name == "getCanonicalText" }
                                            .invoke(result, false) as String
                                    )
                                }
                            }
                        }
                        method.returnType.name == "byte" -> {
                            val value = { result.asByte() }
                            cache.getOrPut(Pair(method.returnType, result), value)
                        }
                        method.returnType.name == "short" -> {
                            val value = { result.asShort() }
                            cache.getOrPut(Pair(method.returnType, result), value)
                        }
                        method.returnType.name == "long" -> {
                            val value = { result.asLong() }
                            cache.getOrPut(Pair(method.returnType, result), value)
                        }
                        method.returnType.name == "float" -> {
                            val value = { result.asFloat() }
                            cache.getOrPut(Pair(method.returnType, result), value)
                        }
                        method.returnType.name == "double" -> {
                            val value = { result.asDouble() }
                            cache.getOrPut(Pair(method.returnType, result), value)
                        }
                        else -> result // original value
                    }
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun List<*>.asArray(method: Method, proxyClass: Class<*>) =
    when (method.returnType.componentType.name) {
        "boolean" -> (this as List<Boolean>).toBooleanArray()
        "byte" -> (this as List<Byte>).toByteArray()
        "short" -> (this as List<Short>).toShortArray()
        "char" -> (this as List<Char>).toCharArray()
        "double" -> (this as List<Double>).toDoubleArray()
        "float" -> (this as List<Float>).toFloatArray()
        "int" -> (this as List<Int>).toIntArray()
        "long" -> (this as List<Long>).toLongArray()
        "java.lang.Class" -> (this as List<KSType>).asClasses(proxyClass).toTypedArray()
        "java.lang.String" -> (this as List<String>).toTypedArray()
        else -> { // arrays of enums or annotations
            when {
                method.returnType.componentType.isEnum -> {
                    this.toArray(method) { result -> result.asEnum(method.returnType.componentType) }
                }
                method.returnType.componentType.isAnnotation -> {
                    this.toArray(method) { result ->
                        (result as KSAnnotation).asAnnotation(method.returnType.componentType)
                    }
                }
                else -> throw IllegalStateException("Unable to process type ${method.returnType.componentType.name}")
            }
        }
    }

@Suppress("UNCHECKED_CAST")
private fun List<*>.toArray(method: Method, valueProvider: (Any) -> Any): Array<Any?> {
    val array: Array<Any?> = java.lang.reflect.Array.newInstance(
        method.returnType.componentType,
        this.size
    ) as Array<Any?>
    for (r in indices) {
        array[r] = this[r]?.let { valueProvider.invoke(it) }
    }
    return array
}

@Suppress("UNCHECKED_CAST")
private fun <T> Any.asEnum(returnType: Class<T>): T =
    returnType.getDeclaredMethod("valueOf", String::class.java)
        .invoke(
            null,
            when (this) {
                is KSType -> this.declaration.simpleName.getShortName()
                is KSClassDeclaration -> this.simpleName.getShortName()
                else -> this.toString()
            }
        ) as T

private fun Any.asByte(): Byte = if (this is Int) this.toByte() else this as Byte

private fun Any.asShort(): Short = if (this is Int) this.toShort() else this as Short

private fun Any.asLong(): Long = if (this is Int) this.toLong() else this as Long

private fun Any.asFloat(): Float = if (this is Int) this.toFloat() else this as Float

private fun Any.asDouble(): Double = if (this is Int) this.toDouble() else this as Double

private fun Any.asArray(method: Method, proxyClass: Class<*>) = listOf(this).asArray(method, proxyClass)

private fun KSAnnotation.asAnnotation(
    annotationInterface: Class<*>,
): Any {
    return Proxy.newProxyInstance(
        annotationInterface.classLoader, arrayOf(annotationInterface),
        this.createInvocationHandler(annotationInterface)
    ) as Proxy
}

private fun List<KSType>.asClasses(proxyClass: Class<*>) = try {
    this.map { type -> type.asClass(proxyClass) }
} catch (e: Exception) {
    throw KSTypesNotPresentException(this, e)
}

private fun KSType.asClass(proxyClass: Class<*>) = try {
    Class.forName(this.declaration.qualifiedName!!.asString(), true, proxyClass.classLoader)
} catch (e: Exception) {
    throw KSTypeNotPresentException(this, e)
}

class KSTypeNotPresentException(val ksType: KSType, cause: Throwable) : RuntimeException(cause)

class KSTypesNotPresentException(val ksTypes: List<KSType>, cause: Throwable) : RuntimeException(cause)

fun KSDeclaration.toClassName(): ClassName {
    return ClassName(packageName.asString(), simpleName.asString())
}

fun String.toTranslateString():String {
    return "\"" + replace("\\", "\\\\") + "\""
}

operator fun OutputStream.plusAssign(str: String) = write(str.toByteArray())

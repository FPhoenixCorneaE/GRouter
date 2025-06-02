package com.fphoenixcorneae.grouter.plugin

import org.gradle.api.file.Directory
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.InstructionAdapter

class RouteRegisterMethodVisitor(
    nextVisitor: ClassVisitor,
    private val inputDirs: List<Directory>,
    private val genDirName: String
) : ClassVisitor(Opcodes.ASM9, nextVisitor) {

    private val classNamePrefix by lazy {
        Constant.ROUTER_GENERATE_PACKAGE_NAME
            .replace(".", "/")
            .plus("/")
            .plus(Constant.ROUTER_GENERATE_CLASS_NAME_PREFIX)
    }

    private val validClasses by lazy {
        inputDirs.asSequence()
            .flatMap { dir -> dir.asFileTree.matching { it.include("**/**.kt") } }
            .mapNotNull { file ->
                return@mapNotNull file.absolutePath
                    .replace("\\", "/")
                    .substringAfter(genDirName)
                    .substringAfter("kotlin/")
                    .removeSuffix(".kt")
                    .takeIf { it.startsWith(classNamePrefix) }
            }
            .toList()
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == Constant.ROUTER_REGISTER_METHOD_NAME) {
            println("Router plugin find RouteRegister.registerRoutes() method")
            return object : InstructionAdapter(Opcodes.ASM9, mv) {
                override fun visitCode() {
                    validClasses.forEach { clazz ->
                        println("Router plugin call $clazz.register() method")
                        invokestatic(
                            clazz,
                            Constant.ROUTER_METHOD_NAME,
                            "()V",
                            false
                        )
                    }
                    super.visitCode()
                }
            }
        }
        return mv
    }
}
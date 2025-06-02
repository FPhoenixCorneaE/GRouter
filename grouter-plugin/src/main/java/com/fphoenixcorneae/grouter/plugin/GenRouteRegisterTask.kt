package com.fphoenixcorneae.grouter.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenRouteRegisterTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val filePath = Constant.ROUTER_REGISTER_CLASS_NAME
            .replace(".", "/")
            .plus(".kt")
        val outputFile = File(outputFolder.asFile.get(), filePath)
        outputFile.parentFile.mkdirs()
        outputFile.writeText(Constant.ROUTER_REGISTER_CLASS_CONTENT)
        println("Router plugin write RouteRegister class content")
    }
}
package com.fphoenixcorneae.grouter.plugin

import com.android.build.api.AndroidPluginVersion
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.fphoenixcorneae.grouter.plugin.factory.RouterClassVisitorFactory
import com.fphoenixcorneae.grouter.plugin.task.GenRouteRegisterTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

class RouterPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)

        require(isApp) { "GRouter plugin only support application module" }

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        println("-------- GRouter plugin Env --------")
        println("Gradle version ${project.gradle.gradleVersion}")
        println("${androidComponents.pluginVersion}")
        println("JDK version ${System.getProperty("java.version")}")

        require(androidComponents.pluginVersion >= AndroidPluginVersion(7, 4, 0)) {
            "AGP version must be at least 7.4 or higher. current version ${androidComponents.pluginVersion}"
        }

        androidComponents.onVariants { variant ->
            // 任务名称，`variant` 名称首字母大写
            val taskName = "GenRouterRegister${variant.name.capitalized()}"
            val addSourceTaskProvider =
                project.tasks.register(taskName, GenRouteRegisterTask::class.java)
            variant.sources.java?.addGeneratedSourceDirectory(
                addSourceTaskProvider,
                GenRouteRegisterTask::outputFolder
            )

            val generatedDir = "generated/ksp/"
            variant.instrumentation.transformClassesWith(
                RouterClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) { param ->
                param.genDirName.set(generatedDir)
                val list = project.rootProject.subprojects.plus(project)
                    .map { it.layout.buildDirectory.dir(generatedDir).get() }
                param.inputFiles.set(list)
            }
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            variant.instrumentation.excludes.addAll(
                "androidx/**",
                "android/**",
                "com/google/**",
            )
        }
    }
}
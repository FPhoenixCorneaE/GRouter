plugins {
    id("java-library")
    id("java-gradle-plugin")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.valueOf(libs.versions.javaVersion.get())
    targetCompatibility = JavaVersion.valueOf(libs.versions.javaVersion.get())
}

dependencies {
    implementation(gradleApi())
    compileOnly(libs.gradle)
    implementation(libs.asm)
    implementation(libs.asm.commons)
}

gradlePlugin {
    plugins {
        create("grouter-plugin") {
            id = "grouter-plugin"
            implementationClass = "com.fphoenixcorneae.grouter.plugin.RouterPlugin"
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId = "com.github.FPhoenixCorneaE"
                artifactId = "grouter-plugin"
                version = libs.versions.grouter.get()
            }
        }
//        repositories {
//            maven {
//                url = uri("../localRepos")
//            }
//        }
    }
}
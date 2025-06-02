plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId = "com.github.FPhoenixCorneaE"
                artifactId = "grouter-annotation"
                version = libs.versions.grouter.get()
            }
        }
    }
}
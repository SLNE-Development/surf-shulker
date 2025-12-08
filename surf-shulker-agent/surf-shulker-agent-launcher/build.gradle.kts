//import org.springframework.boot.gradle.tasks.bundling.BootJar
//
plugins {
    id("dev.slne.surf.surfapi.gradle.standalone")
    id("exclude-kotlin")

    application
    alias(libs.plugins.spring.boot)
}
//
//surfStandaloneApi {
//    addSurfApiToClasspath(false)
//}
//
//application {
//    mainClass.set("dev.slne.surf.shulker.server.launcher.Launcher")
//}
//
//tasks {
//    val serverProject = project(":surf-shulker-server:surf-shulker-server")
//    val serverJarTask = serverProject.tasks.named<BootJar>("bootJar")
//
//    val copyServerJar by registering(Copy::class) {
//        dependsOn(serverJarTask)
//
//        from(serverJarTask.flatMap { it.archiveFile })
//        into(layout.buildDirectory.dir("libs"))
//        rename { "surf-shulker-server.jar.disabled" }
//
//        inputs.file(serverJarTask.flatMap { it.archiveFile })
//        outputs.dir(layout.buildDirectory.file("libs/surf-shulker-server.jar.disabled"))
//
//        doFirst {
//            val serverJar = serverJarTask.get().archiveFile.get().asFile
//
//            if (!serverJar.exists()) {
//                throw GradleException("Server JAR not found: ${serverJar.absolutePath}")
//            }
//        }
//    }
//
//    val cleanupServerJar by registering(Delete::class) {
//        delete(
//            layout.buildDirectory.dir("libs")
//                .map { it.file("surf-shulker-server.jar.disabled") })
//    }
//
//    bootJar {
//        dependsOn(copyServerJar)
//        dependsOn(serverJarTask)
//
//        from(layout.buildDirectory.file("libs/surf-shulker-server.jar.disabled"))
//        from(resources.text.fromString("org.springframework.boot.loader.launch.JarLauncher")) {
//            into("META-INF/main")
//            rename { "main-class" }
//        }
//
//        from(layout.buildDirectory.file("classes/java/main/dev/slne/surf/shulker/server/launcher/LauncherAgent.class")) {
//            into("dev/slne/surf/shulker/server/launcher")
//        }
//
//        val springInstrumentJar = configurations.runtimeClasspath.get()
//            .find { it.name.contains("spring-instrument") }
//            ?: throw GradleException("spring-instrument JAR not found in runtimeClasspath")
//
//        from(zipTree(springInstrumentJar)) {
//            include("org/springframework/instrument/InstrumentSavingAgent.class")
//            into("")
//        }
//
//        manifest {
//            attributes(
//                "Launcher-Agent-Class" to "dev.slne.surf.shulker.server.launcher.LauncherAgent",
//            )
//        }
//
//        doLast {
//            file(layout.buildDirectory.file("libs/surf-shulker-server.jar.disabled")).delete()
//        }
//
//        finalizedBy(cleanupServerJar)
//    }
//}
//
//dependencies {
//    implementation(libs.bundles.maven.libraries)
//    implementation(libs.spring.instrument)
//}
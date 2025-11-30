import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.slne.surf.surfapi.gradle.util.slnePublic

buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.10+")
    }
}

plugins {
//    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.17.0"
    java
}

allprojects {
    group = "dev.slne.surf.shulker"
    version = findProperty("version") as String

    repositories {
        slnePublic()
    }

    if (name == "surf-shulker-bom") {
        return@allprojects
    }

    apply(plugin = "java")

    dependencies {
        implementation(platform(project(":surf-shulker-bom")))
        compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.5.6")
    }

    tasks {
        configureShadowJar()
        configureJar()

        javadoc {
            // Temp disable this as it seems to not work with proto generated files, investigate later
            enabled = false

            val options = options as StandardJavadocDocletOptions

            options.use()
            options.tags("implNote:a:Implementation Note:")
        }
    }
}

//apiValidation {
//    ignoredProjects.addAll(listOf())
//}

private fun TaskContainerScope.configureShadowJar() = withType<ShadowJar> {
    mergeServiceFiles {
        path = "META-INF"
        exclude("META-INF/MANIFEST.MF")
    }

    isZip64 = true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

private fun TaskContainerScope.configureJar() = withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
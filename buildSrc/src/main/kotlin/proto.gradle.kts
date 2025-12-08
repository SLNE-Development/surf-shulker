import com.google.protobuf.gradle.ProtobufExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`

    id("com.google.protobuf")
}

val libs = the<LibrariesForLibs>()
dependencies {
    compileOnlyApi(libs.grpc.stub)
    compileOnlyApi(libs.grpc.protobuf)
    compileOnlyApi(libs.protobuf.java.util)
    compileOnlyApi(libs.protobuf.kotlin)
    compileOnlyApi(libs.grpc.kotlin.stub)
}

extensions.configure<ProtobufExtension> {
    protoc {
        artifact = libs.protoc.asProvider().get().toString()
    }

    plugins {
        create("grpc") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
        create("grpckt") {
            artifact = libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
                create("grpckt")
            }
            task.builtins {
                create("kotlin")
            }
        }
    }
}
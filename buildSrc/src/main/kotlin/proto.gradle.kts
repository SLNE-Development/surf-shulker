import com.google.protobuf.gradle.ProtobufExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`

    id("com.google.protobuf")
}

val libs = the<LibrariesForLibs>()
dependencies {
    implementation(libs.grpc.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.protobuf.java.util)
    implementation(libs.protobuf.kotlin)
    implementation(libs.grpc.kotlin.stub)
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
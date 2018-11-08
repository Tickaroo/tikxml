plugins {
    java
    id("kotlin")
}

apply(from = "$rootDir/maven-push-java-lib.gradle")

dependencies {
    compile(project(":annotation"))
    compile(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)
}

plugins {
    java
    id("kotlin")
}

// apply(from = "$rootDir/maven-push-java-lib.gradle")
apply (plugin = "com.vanniktech.maven.publish")

dependencies {
    compile(project(":annotation"))
    compile(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)
}

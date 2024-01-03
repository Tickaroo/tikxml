plugins {
    `java-library`
    id("kotlin")
    id("com.vanniktech.maven.publish")
}

// apply(from = "$rootDir/maven-push-java-lib.gradle")
// apply (plugin = "com.vanniktech.maven.publish")

dependencies {
    api(project(":annotation"))
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)
}

plugins {
    java
    id("com.vanniktech.maven.publish")
}

// apply(plugin = "com.vanniktech.maven.publish")

dependencies {
    implementation(project(":core"))
    implementation(project(":annotation"))
    implementation(Deps.retrofit)
    implementation(Deps.kotlinStdLib)
    implementation(project(":processor-common"))
    testAnnotationProcessor(project(":processor"))
    testImplementation(Deps.mockWebserver)
    testImplementation(Deps.junit)
}

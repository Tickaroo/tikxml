plugins {
    `java-library`
    id("com.vanniktech.maven.publish")
}

// apply(plugin = "com.vanniktech.maven.publish")

dependencies {
    api(project(":core"))
    api(project(":annotation"))
    api(Deps.retrofit)
    api(Deps.kotlinStdLib)
    implementation(project(":processor-common"))
    testAnnotationProcessor(project(":processor"))
    testImplementation(Deps.mockWebserver)
    testImplementation(Deps.junit)
}

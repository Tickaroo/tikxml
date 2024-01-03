plugins {
    `java-library`
    id("kotlin")
    id("com.vanniktech.maven.publish")
}

// apply(from = "$rootDir/maven-push-java-lib.gradle")
// apply (plugin = "com.vanniktech.maven.publish")

dependencies {
    api(project(":core"))
    api(project(":annotation"))
    api(project(":processor-common"))

    compileOnly(Deps.incrementalAnnotation)

    implementation(Deps.guava)
    implementation(Deps.javaPoet)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)
    implementation(Deps.autoServiceAnnotation)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.mockito)
    testImplementation(Deps.compileTesting)
    testImplementation(Deps.kotlinTestJunit)

    annotationProcessor(Deps.autoServiceProcessor)
    annotationProcessor(Deps.incrementalProcessor)
}

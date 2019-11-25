plugins {
    java
    id("kotlin")
}

// apply(from = "$rootDir/maven-push-java-lib.gradle")
apply (plugin = "com.vanniktech.maven.publish")


dependencies {
    compile(project(":core"))
    compile(project(":annotation"))
    compile(project(":processor-common"))

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.1.0")
    implementation(Deps.guava)
    implementation(Deps.javaPoet)
    implementation(Deps.autoServiceAnnotation)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.mockito)
    testImplementation(Deps.compileTesting)
    testImplementation(Deps.kotlinTestJunit)

    annotationProcessor(Deps.autoServiceProcessor)
}

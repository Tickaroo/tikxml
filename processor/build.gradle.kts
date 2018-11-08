plugins {
    java
    id("kotlin")
}

apply(from = "$rootDir/maven-push-java-lib.gradle")

dependencies {
    compile(project(":core"))
    compile(project(":annotation"))
    compile(project(":processor-common"))

    implementation(Deps.guava)
    implementation(Deps.javaPoet)
    implementation(Deps.autoService)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.mockito)
    testImplementation(Deps.compileTesting)
    testImplementation(Deps.kotlinTestJunit)
}

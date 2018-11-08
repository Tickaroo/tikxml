plugins {
    java
}

apply(from = "$rootDir/maven-push-java-lib.gradle")

dependencies {
    compile(project(":core"))
    compile(project(":annotation"))
    compile(Deps.retrofit)
    compile(Deps.kotlinStdLib)
    implementation(project(":processor-common"))
    testAnnotationProcessor(project(":processor"))
    testImplementation(Deps.mockWebserver)
    testImplementation(Deps.junit)
}

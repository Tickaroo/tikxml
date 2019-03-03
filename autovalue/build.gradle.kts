plugins {
    java
    id("kotlin")
}
// apply(from = "../maven-push-java-lib.gradle")
apply (plugin = "com.vanniktech.maven.publish")

dependencies {
    compile(project(":core"))
    compile(project(":annotation"))
    compile(project(":processor-common"))
    compile(Deps.autoValueAnnotation)
    implementation(Deps.javaPoet)
    implementation(Deps.autoService)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.autoValueProcessor)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.compileTesting)
}

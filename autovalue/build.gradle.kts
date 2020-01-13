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
    implementation(Deps.kotlinStdLib)
    implementation(Deps.autoValueProcessor)
    implementation(Deps.autoServiceAnnotation)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.compileTesting)

    annotationProcessor(Deps.autoServiceProcessor)
}

plugins {
    `java-library`
    id("kotlin")
    id("com.vanniktech.maven.publish")
}
// apply(from = "../maven-push-java-lib.gradle")
//apply (plugin = "com.vanniktech.maven.publish")

dependencies {
    api(project(":core"))
    api(project(":annotation"))
    api(project(":processor-common"))
    api(Deps.autoValueAnnotation)
    implementation(Deps.javaPoet)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.autoValueProcessor)
    implementation(Deps.autoServiceAnnotation)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.compileTesting)

    annotationProcessor(Deps.autoServiceProcessor)
}

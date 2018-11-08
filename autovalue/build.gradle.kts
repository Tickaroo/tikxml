plugins {
    java
    id("kotlin")
}

dependencies {
    compile(project(":core"))
    compile(project(":annotation"))
    compile(project(":processor-common"))
    compile(Deps.autoValueAnnotation)
    implementation(Deps.javaPoet)
    implementation(Deps.autoService)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)
    implementation(Deps.autoValueProcessor)

    testImplementation(Deps.junit)
    testImplementation(Deps.truth)
    testImplementation(Deps.compileTesting)
}

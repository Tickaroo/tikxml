plugins {
    java
}

dependencies {
    implementation(project(":core"))
    implementation(project(":processor"))
    implementation(project(":annotation"))
    implementation(project(":processor-common"))

    implementation(Deps.caliper)
    implementation(Deps.javaPoet)
    implementation(Deps.simpleXml)
    implementation(Deps.jacksonXml)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)
}

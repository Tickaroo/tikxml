plugins {
    java
    id("kotlin")
}

dependencies {
    compile(project(":annotation"))
    compile(Deps.kotlinStdLib)
    implementation(Deps.kotlinReflect)
}

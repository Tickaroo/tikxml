plugins {
    java
}

dependencies {
    implementation(project(":autovalue"))
    implementation(Deps.autoValueProcessor)
    annotationProcessor(project(":processor"))
    annotationProcessor(project(":autovalue"))

    testImplementation(Deps.junit)
}

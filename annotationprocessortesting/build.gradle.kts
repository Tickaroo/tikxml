plugins {
    java
}

dependencies {
    implementation(project(":autovalue"))
    annotationProcessor(project(":processor"))
    annotationProcessor(project(":autovalue"))

    testImplementation(Deps.junit)
}

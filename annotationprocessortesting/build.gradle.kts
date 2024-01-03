plugins {
    `java-library`
}

dependencies {
    implementation(project(":autovalue"))
    annotationProcessor(project(":processor"))
    annotationProcessor(project(":autovalue"))

    testImplementation(Deps.junit)
}

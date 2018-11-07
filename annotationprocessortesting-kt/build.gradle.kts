plugins {
    java
    id("kotlin")
    id("kotlin-kapt")
}

dependencies {
    implementation(Deps.kotlinStdLib)
    implementation(project(":autovalue"))
    kapt(project(":processor"))
    kapt(project(":autovalue"))

    testImplementation(Deps.junit)
}

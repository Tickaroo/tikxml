plugins {
    `java-library`
    id("com.vanniktech.maven.publish")
}

// apply(from = "$rootDir/maven-push-java-lib.gradle")
// apply (plugin = "com.vanniktech.maven.publish")

dependencies {
    api(Deps.okio)
    testImplementation(Deps.junit)
    testImplementation(Deps.assertj)
}

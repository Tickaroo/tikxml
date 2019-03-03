plugins {
    java
}

// apply(from = "$rootDir/maven-push-java-lib.gradle")
apply (plugin = "com.vanniktech.maven.publish")

dependencies {
    compile(Deps.okio)
    testImplementation(Deps.junit)
    testImplementation(Deps.assertj)
}

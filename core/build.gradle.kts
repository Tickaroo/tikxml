plugins {
    java
}

apply(from = "$rootDir/maven-push-java-lib.gradle")

dependencies {
    compile(Deps.okio)
    testImplementation(Deps.junit)
    testImplementation(Deps.assertj)
}

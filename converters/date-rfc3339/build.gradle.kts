plugins {
    java
}

apply(from = "$rootDir/maven-push-java-lib.gradle")


dependencies {
    implementation(project(":core"))
    testCompile(Deps.junit)
}

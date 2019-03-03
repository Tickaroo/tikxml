plugins {
    java
}

apply (plugin = "com.vanniktech.maven.publish")

// apply(from = "../maven-push-java-lib.gradle")

dependencies {
    compile(project(":core"))
}

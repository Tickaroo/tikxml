plugins {
    `java-library`
    id("com.vanniktech.maven.publish")
}

//apply (plugin = "com.vanniktech.maven.publish")
// apply(from = "../maven-push-java-lib.gradle")

dependencies {
    api(project(":core"))
}

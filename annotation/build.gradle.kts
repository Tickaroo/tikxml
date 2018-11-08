plugins {
    java
}

apply(from = "../maven-push-java-lib.gradle")

dependencies {
   compile(project(":core"))
}

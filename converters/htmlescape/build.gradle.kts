plugins {
  java
}

// apply(from = "$rootDir/maven-push-java-lib.gradle")
apply(plugin = "com.vanniktech.maven.publish")

dependencies {
  implementation(project(":core"))
  testImplementation(Deps.junit)
}

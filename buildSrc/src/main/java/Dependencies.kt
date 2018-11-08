object Versions {
    const val autoService = "1.0-rc4"
    const val autoValue = "1.6.2"
    const val junit = "4.12"
    const val kotlin = "1.3.0"
    const val javaPoet = "1.11.1"
    const val compileTesting = "0.15"
    const val truth = "0.42"
    const val jackson = "2.9.7"
    const val simpleXml = "2.7.1"
    const val caliper = "1.2.1"
    const val okio = "2.1.0"
    const val assertj = "3.11.1"
    const val guava = "27.0-jre"
    const val mockito = "2.23.0"
    const val retrofit = "2.4.0"
    const val mockWebserver = "3.11.0"
}

object Deps {
    // testing
    const val junit = "junit:junit:${Versions.junit}"
    const val compileTesting = "com.google.testing.compile:compile-testing:${Versions.compileTesting}"
    const val truth = "com.google.truth:truth:${Versions.truth}"
    const val assertj = "org.assertj:assertj-core:${Versions.assertj}"
    const val mockito = "org.mockito:mockito-core:${Versions.mockito}"

    // google auto
    const val autoService ="com.google.auto.service:auto-service:${Versions.autoService}"
    const val autoValueProcessor = "com.google.auto.value:auto-value:${Versions.autoValue}"
    const val autoValueAnnotation = "com.google.auto.value:auto-value-annotations:${Versions.autoValue}"

    // kotlin
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"

    // okio
    const val okio = "com.squareup.okio:okio:${Versions.okio}"

    // retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"

    // mockwebserver
    const val mockWebserver = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebserver}"

    // guava
    const val guava = "com.google.guava:guava:${Versions.guava}"

    // parser
    const val jacksonXml = "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.jackson}"
    const val simpleXml = "org.simpleframework:simple-xml:${Versions.simpleXml}"

    // code generation
    const val javaPoet = "com.squareup:javapoet:${Versions.javaPoet}"

    // benachmark
    const val caliper = "net.trajano.caliper:caliper:${Versions.caliper}"
}
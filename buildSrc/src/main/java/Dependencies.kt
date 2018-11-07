object Versions {
    const val autoService = "1.0-rc4"
    const val autoValue = "1.6.2"
    const val junit = "4.12"
    const val kotlin = "1.3.0"
    const val javaPoet = "1.11.1"
    const val compileTesting = "0.15"
    const val truth = "0.42"
}

object Deps {
    // testing
    const val junit = "junit:junit:${Versions.junit}"
    const val compileTesting = "com.google.testing.compile:compile-testing:${Versions.compileTesting}"
    const val truth = "com.google.truth:truth:${Versions.truth}"

    // google auto
    const val autoService ="com.google.auto.service:auto-service:${Versions.autoService}"
    const val autoValueProcessor = "com.google.auto.value:auto-value:${Versions.autoValue}"
    const val autoValueAnnotation = "com.google.auto.value:auto-value-annotations:${Versions.autoValue}"

    // kotlin
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"

    const val javaPoet = "com.squareup:javapoet:${Versions.javaPoet}"
}
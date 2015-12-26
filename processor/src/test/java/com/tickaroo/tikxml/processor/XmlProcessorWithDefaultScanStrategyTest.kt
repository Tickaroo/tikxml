/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml.processor

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import com.google.testing.compile.JavaSourcesSubject
import com.tickaroo.tikxml.annotation.Xml
import org.junit.Ignore
import org.junit.Test
import javax.tools.JavaFileObject

/**
 *
 * @author Hannes Dorfmann
 */
class XmlProcessorWithDefaultScanStrategyTest {

    @Test
    fun noPublicConstructor() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class NoConstructorClass { private NoConstructorClass() {} }")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("must provide an empty (parameterless) constructor with minimum default (package) visibility")
    }

    @Test
    fun noParameterlessConstructor() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoParameterlessConstructor",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class NoParameterlessConstructor { public NoParameterlessConstructor(int a) {} }")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("must provide an empty (parameterless) constructor with minimum default (package) visibility")
    }

    @Test
    @Ignore
            // TODO enable once code generation works
    fun defaultVisibilityConstructor() {
        val componentFile = JavaFileObjects.forSourceLines("test.DefaultConstTest",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class DefaultConstTest {",
                " private DefaultConstTest(int a) {}",
                " DefaultConstTest() {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    @Ignore
            // TODO enable once code generation works
    fun publicVisibilityConstructor() {
        val componentFile = JavaFileObjects.forSourceLines("test.PublicConstTest",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class PublicConstTest {",
                " private PublicConstTest(int a) {}",
                " public PublicConstTest() {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }
}
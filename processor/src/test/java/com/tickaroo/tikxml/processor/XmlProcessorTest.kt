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
import org.junit.Test
import javax.tools.JavaFileObject
import kotlin.test.assertEquals

/**
 *
 * @author Hannes Dorfmann
 */
class XmlProcessorTest {

    @Test
    fun annotatingInterface() {
        val componentFile = JavaFileObjects.forSourceLines("test.NotAClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "interface NotAClass {}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Only classes can be annotated with")
    }

    @Test
    fun annotatingEnum() {
        val componentFile = JavaFileObjects.forSourceLines("test.NotAClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "enum NotAClass {}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Only classes can be annotated with")
    }

    @Test
    fun abstractClass() {
        val componentFile = JavaFileObjects.forSourceLines("test.AbstractClass",
                "package test;",
                "",
                "@${Xml::class.qualifiedName}",
                "abstract class AbstractClass {}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun typeConverterForPrimitiveTypesNoOptions(){
        val processor = XmlProcessor()
        assertEquals(emptySet<String>(), processor.readPrimitiveTypeConverterOptions(null))
        assertEquals(emptySet<String>(), processor.readPrimitiveTypeConverterOptions(""))
    }

    @Test
    fun typeConverterForPrimitiveTypesSingle(){

        val expectedOptions = setOf("java.lang.String")
        val processor = XmlProcessor()
        assertEquals(expectedOptions, processor.readPrimitiveTypeConverterOptions("java.lang.String"))

    }

    @Test
    fun typeConverterForPrimitiveTypesMultiple(){

        val expectedOptions = setOf("java.lang.String", "java.lang.int", "java.lang.Integer")
        val processor = XmlProcessor()
        assertEquals(expectedOptions, processor.readPrimitiveTypeConverterOptions("java.lang.String, java.lang.int, java.lang.Integer"))
    }

    @Test
    fun typeConverterForPrimitiveTypesMultipleTrim(){

        val expectedOptions = setOf("java.lang.String", "java.lang.int", "java.lang.Integer")
        val processor = XmlProcessor()
        assertEquals(expectedOptions, processor.readPrimitiveTypeConverterOptions("  java.lang.String,    java.lang.int  ,    java.lang.Integer   "))
    }
}
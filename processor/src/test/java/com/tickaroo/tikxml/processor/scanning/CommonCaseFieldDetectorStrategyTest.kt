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

package com.tickaroo.tikxml.processor.scanning

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import com.google.testing.compile.JavaSourcesSubject
import com.tickaroo.tikxml.annotation.IgnoreXml
import com.tickaroo.tikxml.annotation.ScanMode
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.XmlProcessor
import org.junit.Test
import javax.tools.JavaFileObject

/**
 *
 * @author Hannes Dorfmann
 */
class CommonCaseFieldDetectorStrategyTest {

    @Test
    fun attributesOnly() {
        val componentFile = JavaFileObjects.forSourceLines("test.AttributeClass",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class AttributeClass {",
                "   int a;",
                "",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun elementsOnly() {
        val componentFile = JavaFileObjects.forSourceLines("test.AttributeClass",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class AttributeClass {",
                "   Other a;",
                "   Other b;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class Other {",
                " int b;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun elementsAndAttributes() {
        val componentFile = JavaFileObjects.forSourceLines("test.AttributeClass",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class AttributeClass {",
                "   Other a;",
                "   Other b;",
                "   int c;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class Other {",
                " int b;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun nameConflictInheritance() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflictInheritance",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class NameConflictInheritance extends Parent {",
                "   int a;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class Parent {",
                "   String a;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: The field 'a' in class test.Parent has the same XML name 'a' as the field 'a' in class test.NameConflictInheritance. You can specify another name via annotations.")
    }

    @Test
    fun nameConflictInheritanceOff() {
        val componentFile = JavaFileObjects.forSourceLines("test.InheritanceOff",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(inheritance = false, scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class InheritanceOff extends Parent {",
                "   int a;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class Parent {",
                "   String a;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun nameConflictIgnoreAnnotation() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflictIgnoreAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class NameConflictIgnoreAnnotation extends Parent {",
                "   int a;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.COMMON_CASE})",
                "class Parent {",
                "   @${IgnoreXml::class.qualifiedName}",
                "   String a;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

}
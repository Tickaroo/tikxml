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
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.XmlProcessor
import org.junit.Test
import javax.tools.JavaFileObject

/**
 * Tests [FieldScanner]:
 * - empty Consturctor
 * - Getter Methods
 * - Setter Methods
 * @author Hannes Dorfmann
 */
class FieldScannerTest {

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

    @Test
    fun noPublicGetter() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoPublicGetter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NoPublicGetter {",
                "   @${Attribute::class.qualifiedName}",
                "   private int a;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.NoPublicGetter has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getA() or isA() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun noPublicSetter() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoPublicSetter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NoPublicSetter {",
                "   @${Attribute::class.qualifiedName}",
                "   private int a;",
                "   public int getA(){ return a;}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.NoPublicSetter has private or protected visibility. Hence a corresponding setter method must be provided  with the name setA(int) and minimum package visibility (or public visibility if this is a super class in a different package)Unfortunately, there is no such setter method. Please provide one!")
    }

    @Test
    fun publicGetterAndSetter() {
        val componentFile = JavaFileObjects.forSourceLines("test.SetterAndGetter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class SetterAndGetter {",
                "   @${Attribute::class.qualifiedName}",
                "   private int a;",
                "   public int getA(){ return a;}",
                "   public void setA(int a){}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun wrongSetterParameter() {
        val componentFile = JavaFileObjects.forSourceLines("test.WrongSetterParam",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class WrongSetterParam {",
                "   @${Attribute::class.qualifiedName}",
                "   private int a;",
                "   public int getA(){ return a;}",
                "   public void setA(String a){}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The setter method 'setA(java.lang.String)' for field 'a' in class test.WrongSetterParam must have exactly one parameter of type 'int'")
    }

    @Test
    fun privateGetter() {
        val componentFile = JavaFileObjects.forSourceLines("test.PrivateGetter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PrivateGetter {",
                "   @${Attribute::class.qualifiedName}",
                "   private int a;",
                "   private int getA(){ return a;}",
                "   public void setA(int a){}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.PrivateGetter has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getA() or isA() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun privateSetter() {
        val componentFile = JavaFileObjects.forSourceLines("test.PrivateSetter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PrivateSetter {",
                "   @${Attribute::class.qualifiedName}",
                "   private int a;",
                "   public int getA(){ return a;}",
                "   private void setA(int a){}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.PrivateSetter has private or protected visibility. Hence a corresponding setter method must be provided  with the name setA(int) and minimum package visibility (or public visibility if this is a super class in a different package)Unfortunately, there is no such setter method. Please provide one!")

    }

    @Test
    fun defaultVisibility() {
        val componentFile = JavaFileObjects.forSourceLines("test.PrivateSetter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PrivateSetter {",
                "   @${Attribute::class.qualifiedName}",
                "   private int a;",
                "   int getA(){ return a;}",
                "   void setA(int a){}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun protectedField() {
        val componentFile = JavaFileObjects.forSourceLines("test.PrivateSetter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PrivateSetter {",
                "   @${Attribute::class.qualifiedName}",
                "   protected int a;",
                "   int getA(){ return a;}",
                "   void setA(int a){}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun privateTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.PrivateTextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PrivateTextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.PrivateTextContent has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getA() or isA() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun protectedTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.ProtectedTextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ProtectedTextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.ProtectedTextContent has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getA() or isA() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun privateGetterTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "   private int getA(){ return a;}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.TextContent has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getA() or isA() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun protectedGetterTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "   protected int getA(){ return a;}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.TextContent has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getA() or isA() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun NoSetterTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "   int getA(){ return a;}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.TextContent has private or protected visibility. Hence a corresponding setter method must be provided  with the name setA(java.lang.String) and minimum package visibility (or public visibility if this is a super class in a different package)Unfortunately, there is no such setter method. Please provide one!")

    }

    @Test
    fun privateSetterTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "   String getA(){ return a;}",
                "   private void setA(String a){}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'a' in class test.TextContent has private or protected visibility. Hence a corresponding setter method must be provided  with the name setA(java.lang.String) and minimum package visibility (or public visibility if this is a super class in a different package)Unfortunately, there is no such setter method. Please provide one!")

    }

    @Test
    fun GetterSetterTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "   String getA(){ return a;}",
                "   void setA(String a){}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun PublicGetterSetterTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   private String a;",
                "   public String getA(){ return a;}",
                "   public void setA(String a){}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }
    

}
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
import com.tickaroo.tikxml.annotation.*
import com.tickaroo.tikxml.processor.XmlProcessor
import org.junit.Test
import javax.tools.JavaFileObject

/**
 * Tests [AnnotationScanner]:
 * - empty Consturctor
 * - Getter Methods
 * - Setter Methods
 * @author Hannes Dorfmann
 */
class AnnotationScannerTest {

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
                "   String getA(){ return a;}",
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

    @Test
    fun privateBooleanField() {
        val componentFile = JavaFileObjects.forSourceLines("test.BooleanField",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class BooleanField {",
                "   @${Attribute::class.qualifiedName}",
                "   private boolean a;",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile().withErrorContaining("The field 'a' in class test.BooleanField has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getA() or isA() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun booleanField() {
        val componentFile = JavaFileObjects.forSourceLines("test.BooleanField",
                "package test;",
                "@${Xml::class.java.canonicalName}",
                "class BooleanField {",
                "   @${Attribute::class.qualifiedName}",
                "   private boolean a;",
                "   boolean isA(){return a; }",
                "   void setA(boolean a) {}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun booleanHungarianField() {
        val componentFile = JavaFileObjects.forSourceLines("test.BooleanField",
                "package test;",
                "@${Xml::class.java.canonicalName}",
                "class BooleanField {",
                "   @${Attribute::class.qualifiedName}",
                "   private boolean mA;",
                "   boolean ismA(){return mA; }",
                "   void setmA(boolean a) {}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun booleanHungarianUpperCaseField() {
        val componentFile = JavaFileObjects.forSourceLines("test.BooleanField",
                "package test;",
                "@${Xml::class.java.canonicalName}",
                "class BooleanField {",
                "   @${Attribute::class.qualifiedName}",
                "   private boolean mA;",
                "   boolean isMA(){return mA; }",
                "   void setMA(boolean a) {}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun wrongReturnType() {
        val componentFile = JavaFileObjects.forSourceLines("test.WrongMethodReturnType",

                "@${Xml::class.qualifiedName}",
                "class WrongMethodReturnType {",
                "   @${Attribute::class.qualifiedName}",
                "   private String foo;",
                "   int getFoo(){return 2; }", // correct getter name, wrong type
                "   void setFoo(String a) {}",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'foo' in class WrongMethodReturnType has private or protected visibility. Hence a corresponding getter method must be provided with minimum package visibility (or public visibility if this is a super class in a different package) with the name getFoo() or isFoo() in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun polymorphicElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   Root element;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicElementConflictingElementName() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=B.class)  ",
                "   })",
                "   Root element;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: A @${ElementNameMatcher::class.simpleName} with the name \"a\" is already mapped to the type PolymorphicElement.A to resolve polymorphism. Hence it cannot be mapped to PolymorphicElement.B as well.")
    }

    @Test
    fun polymorphicElementConflictingWithPropertyElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   Root element;",
                "",
                "    @${PropertyElement::class.qualifiedName}",
                "    String a;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'a' in class PolymorphicElement is in conflict with field 'element' in class PolymorphicElement. Maybe both have the same xml name 'a' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun polymorphicElementConflictingWithElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   Root element;",
                "",
                "    @${Element::class.qualifiedName}(name=\"a\")",
                "    Root a;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'a' in class PolymorphicElement is in conflict with field 'element' in class PolymorphicElement. Maybe both have the same xml name 'a' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun polymorphicElementConflictingWithPathAttribute() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   Root element;",
                "",
                "    @${Path::class.qualifiedName}(\"b\")",
                "    @${Attribute::class.qualifiedName}",
                "    String attribute;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Element field 'element' in class PolymorphicElement can't have attributes that are accessed from outside of the TypeAdapter that is generated from @${Element::class.simpleName} annotated class! Therefore attribute field 'attribute' in class PolymorphicElement can't be added. Most likely the @${Path::class.simpleName} is in conflict with an @${Element::class.simpleName} annotation.")
    }

    @Test
    fun polymorphicElementConflictingWithPathAttribute2() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "    @${Path::class.qualifiedName}(\"b\")",
                "    @${Attribute::class.qualifiedName}",
                "    String attribute;",
                "",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   Root element;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'element' in class PolymorphicElement is in conflict with PolymorphicElement. Maybe both have the same xml name 'b' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }


    /// LIST TESTS

    @Test
    fun polymorphicElementList() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   java.util.List<Root> element;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicElementListConflictingElementName() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=B.class)  ",
                "   })",
                "   java.util.List<Root> element;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: A @${ElementNameMatcher::class.simpleName} with the name \"a\" is already mapped to the type PolymorphicElement.A to resolve polymorphism. Hence it cannot be mapped to PolymorphicElement.B as well.")
    }

    @Test
    fun polymorphicElementInlineListConflictingWithPropertyElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "",
                "   java.util.List<Root> element;",
                "",
                "    @${PropertyElement::class.qualifiedName}",
                "    String a;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'a' in class PolymorphicElement is in conflict with field 'element' in class PolymorphicElement. Maybe both have the same xml name 'a' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun polymorphicElementListNoConflictingWithPropertyElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   java.util.List<Root> element;",
                "",
                "    @${PropertyElement::class.qualifiedName}",
                "    String property;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicElementInlineListConflictingWithElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "",
                "   java.util.List<Root> element;",
                "",
                "    @${Element::class.qualifiedName}(name = \"a\")",
                "    Root a;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'a' in class PolymorphicElement is in conflict with field 'element' in class PolymorphicElement. Maybe both have the same xml name 'a' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun polymorphicElementListNoConflictingWithElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   java.util.List<Root> element;",
                "",
                "    @${Element::class.qualifiedName}",
                "    Root a;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicElementInlineListConflictingWithPathAttribute() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "",
                "   java.util.List<Root> element;",
                "",
                "    @${Path::class.qualifiedName}(\"b\")",
                "    @${Attribute::class.qualifiedName}",
                "    String attribute;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Element field 'element' in class PolymorphicElement can't have attributes that are accessed from outside of the TypeAdapter that is generated from @${Element::class.simpleName} annotated class! Therefore attribute field 'attribute' in class PolymorphicElement can't be added. Most likely the @${Path::class.simpleName} is in conflict with an @${Element::class.simpleName} annotation.")
    }

    @Test
    fun polymorphicElementListNoConflictingWithPathAttribute() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   java.util.List<Root> element;",
                "",
                "    @${Path::class.qualifiedName}(\"a\")",
                "    @${Attribute::class.qualifiedName}",
                "    String attribute;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Element field 'element' in class PolymorphicElement can't have attributes that are accessed from outside of the TypeAdapter that is generated from @${Element::class.simpleName} annotated class! Therefore attribute field 'attribute' in class PolymorphicElement can't be added. Most likely the @${Path::class.simpleName} is in conflict with an @${Element::class.simpleName} annotation.")
    }

    @Test
    fun polymorphicElementListConflictingWithPathAttribute2() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElement",

                "@${Xml::class.qualifiedName}",
                "class PolymorphicElement {",
                "    @${Path::class.qualifiedName}(\"element/a\")",
                "    @${Attribute::class.qualifiedName}",
                "    String attribute;",
                "",
                "    @${Path::class.qualifiedName}(\"element\")",
                "   @${Element::class.qualifiedName} ( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"a\" , type=A.class),  ",
                "     @${ElementNameMatcher::class.qualifiedName}(name=\"b\" , type=B.class)  ",
                "   })",
                "   java.util.List<Root> element;",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class A  extends Root {} ",
                "",
                "   @${Xml::class.qualifiedName}",
                "   static class B extends Root {} ",
                "}")
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'element' in class PolymorphicElement is in conflict with PolymorphicElement. Maybe both have the same xml name 'a' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun privateBooleanFieldWithGetterAndSetters() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass {",
                "    @${Attribute::class.java.simpleName} private boolean attribute;",
                "    boolean isAttribute(){return false;}",
                "    void setAttribute(boolean s){}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun privateBooleanFieldWithIsPrefixWithGetterAndSetters() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass {",
                "    @${Attribute::class.java.simpleName} private boolean isAttribute;",
                "    boolean isAttribute(){return false;}",
                "    void setAttribute(boolean s){}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun validTextContentOnly() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${TextContent::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass {",
                "    @${TextContent::class.java.simpleName} String someContent;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun writeNamespaceContainsDoubleQuote() {

        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(writeNamespaces = {\"\\\"http://test.com\\\"\"})",
                "class NamespaceClass {",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@Xml annotated class NamespaceClass contains an illegal namespace definition \"http://test.com\" . The following characters are not allowed: < > \" ' to be used in a namespace definition")
    }

    @Test
    fun writeNamespaceContainsGreaterChar() {

        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(writeNamespaces = {\"a=http<://test.com\"})",
                "class NamespaceClass {",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@Xml annotated class NamespaceClass contains an illegal namespace definition a=http<://test.com . The following characters are not allowed: < > \" ' to be used in a namespace definition")
    }

    @Test
    fun writeNamespaceContainsLessChar() {

        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(writeNamespaces = {\"a>=http://test.com\"})",
                "class NamespaceClass {",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@Xml annotated class NamespaceClass contains an illegal namespace definition a>=http://test.com . The following characters are not allowed: < > \" ' to be used in a namespace definition")
    }

    @Test
    fun writeNamespaceContainsSingleQuote() {

        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(writeNamespaces = {\"a=http://test.'com\"})",
                "class NamespaceClass {",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@Xml annotated class NamespaceClass contains an illegal namespace definition a=http://test.'com . The following characters are not allowed: < > \" ' to be used in a namespace definition")
    }

    @Test
    fun writeNamespaceContainsMultipleEqualsSigns() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(writeNamespaces = {\"a=b=http://test.com\"})",
                "class NamespaceClass {",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("contains an illegal namespace definition: a=b=http://test.com because it contains more than 1 equals sign (=) character")
    }

    @Test
    fun elementList() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementList",

                "@${Xml::class.qualifiedName}",
                "class ElementList {",
                "   @${Element::class.qualifiedName}",
                "   java.util.List<A> element;",
                "}",
                "",
                "@${Xml::class.qualifiedName}",
                "class A {} "
        )
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun elementListNoCompileTimeChecks() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementList",

                "@${Xml::class.qualifiedName}",
                "class ElementList {",
                "   @${Element::class.qualifiedName}(compileTimeChecks=false)",
                "   java.util.List<A> element;",
                "}",
                "",
                "abstract class A {} "
        )
        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(XmlProcessor())
                .compilesWithoutError()
    }
}
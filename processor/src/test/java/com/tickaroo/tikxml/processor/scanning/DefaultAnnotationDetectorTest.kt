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

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import com.google.testing.compile.JavaSourcesSubject
import com.google.testing.compile.JavaSourcesSubjectFactory
import com.tickaroo.tikxml.annotation.*
import com.tickaroo.tikxml.processor.XmlProcessor
import org.junit.Ignore
import org.junit.Test
import javax.tools.JavaFileObject

/**
 *
 * @author Hannes Dorfmann
 */
class DefaultAnnotationDetectorTest {


    @Test
    fun multipleAnnotationOnField1() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations1",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "import ${PropertyElement::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class MultipleAnnotations1 {",
                "   @${Attribute::class.java.simpleName}",
                "   @${Element::class.java.simpleName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Fields can ONLY be annotated with one of the following")
    }

    @Test
    fun multipleAnnotationOnField2() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations2",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "import ${PropertyElement::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class MultipleAnnotations2 {",
                "   @${Element::class.java.simpleName}",
                "   @${PropertyElement::class.java.simpleName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Fields can ONLY be annotated with one of the following")
    }

    @Test
    fun multipleAnnotationOnField3() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations3",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "import ${PropertyElement::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class MultipleAnnotations3 {",
                "   @${PropertyElement::class.java.simpleName}",
                "   @${Attribute::class.java.simpleName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Fields can ONLY be annotated with one of the following")
    }

    @Test
    fun multipleAnnotationOnField4() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations4",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "import ${PropertyElement::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class MultipleAnnotations4 {",
                "   @${Attribute::class.java.simpleName}",
                "   @${PropertyElement::class.java.simpleName}",
                "   @${Element::class.java.simpleName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Fields can ONLY be annotated with one of the following")
    }

    @Test
    fun multipleAnnotationOnField5() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations4",
                "package test;",
                "",
                "@${Xml::class.qualifiedName}",
                "class MultipleAnnotations4 {",
                "   @${Attribute::class.qualifiedName}",
                "   @${PropertyElement::class.qualifiedName}",
                "   @${Element::class.qualifiedName}",
                "   @${TextContent::class.qualifiedName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Fields can ONLY be annotated with one of the following")
    }

    @Test
    fun multipleAnnotationOnField6() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations4",
                "package test;",
                "",
                "",
                "@${Xml::class.qualifiedName}",
                "class MultipleAnnotations4 {",
                "   @${Attribute::class.qualifiedName}",
                "   @${TextContent::class.qualifiedName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Fields can ONLY be annotated with one of the following annotations @Attribute, @PropertyElement, @Element or @TextContent  and not multiple of them! The field aField in class test.MultipleAnnotations4 is annotated with more than one of these annotations. You must annotate a field with exactly one of these annotations (not multiple)!")
    }

    @Test
    fun multipleAnnotationOnField7() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations4",
                "package test;",
                "",
                "",
                "@${Xml::class.qualifiedName}",
                "class MultipleAnnotations4 {",
                "   @${PropertyElement::class.qualifiedName}",
                "   @${TextContent::class.qualifiedName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Fields can ONLY be annotated with one of the following annotations @Attribute, @PropertyElement, @Element or @TextContent  and not multiple of them! The field aField in class test.MultipleAnnotations4 is annotated with more than one of these annotations. You must annotate a field with exactly one of these annotations (not multiple)!")
    }

    @Test
    fun multipleAnnotationOnField8() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleAnnotations4",
                "package test;",
                "",
                "",
                "@${Xml::class.qualifiedName}",
                "class MultipleAnnotations4 {",
                "   @${TextContent::class.qualifiedName}",
                "   @${Element::class.qualifiedName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
    }

    @Test
    fun failBecauseGenericListTypeNotAnnotated() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnListType",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class InlineListOnListType {",
                "   @${Element::class.java.simpleName}",
                "   java.util.List<Object> aList;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type java.lang.Object used for field 'aList' in test.InlineListOnListType can't be used, because is not annotated with @${Xml::class.simpleName}. Annotate java.lang.Object with @${Xml::class.simpleName}!")
    }

    @Test
    fun inlineListOnArrayListType() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnArrayListType",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class InlineListOnArrayListType {",
                "   @${Element::class.java.canonicalName}",
                "   java.util.ArrayList<Object> aList;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type java.util.ArrayList used for field 'aList' in test.InlineListOnArrayListType can't be used, because is not annotated with @${Xml::class.simpleName}. Annotate java.util.ArrayList with @${Xml::class.simpleName}!")
    }

    @Test
    fun inlineListOnLinkedListType() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnLinkedListType",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class InlineListOnLinkedListType {",
                "   @${Element::class.java.canonicalName}",
                "   java.util.List<Object> aList;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type java.lang.Object used for field 'aList' in test.InlineListOnLinkedListType can't be used, because is not annotated with @${Xml::class.simpleName}. Annotate java.lang.Object with @${Xml::class.simpleName}!")
    }

    @Test
    fun polymorphicTypeIsPrivateClass() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicClassIsPrivate",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicClassIsPrivate {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerPrivateClass.class)",
                "    )",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                "private class InnerPrivateClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@${ElementNameMatcher::class.simpleName} does not allow private classes. test.PolymorphicClassIsPrivate.InnerPrivateClass is a private class!")
    }

    @Test
    fun polymorphicTypeIsProtectedClass() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicClassIsProtected",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicClassIsProtected {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerProtectedClass.class)",
                "    )",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                "protected class InnerProtectedClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@${ElementNameMatcher::class.simpleName} does not allow protected classes. test.PolymorphicClassIsProtected.InnerProtectedClass is a protected class!")
    }

    @Test
    fun polymorphicTypeHasNoPublicConstructor() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicClassHasNoPublicConstructor",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicClassHasNoPublicConstructor {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass {",
                "    private InnerClass() {}",
                " }",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Class test.PolymorphicClassHasNoPublicConstructor.InnerClass used in @${ElementNameMatcher::class.simpleName} must provide an public empty (parameter-less) constructor")
    }

    @Test
    fun polymorphicTypeHasNoEmptyConstructor() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicClassHasNoEmptyConstructor",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicClassHasNoEmptyConstructor {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass {",
                "    public InnerClass(int a) {}",
                " }",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Class test.PolymorphicClassHasNoEmptyConstructor.InnerClass used in @${ElementNameMatcher::class.simpleName} must provide an public empty (parameter-less) constructor")
    }

    @Test
    fun polymorphicTypeIsInterface() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicTypeIsInterface",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicTypeIsInterface {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                " public interface InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@${ElementNameMatcher::class.simpleName} only allows classes. test.PolymorphicTypeIsInterface.InnerClass is a not a class!")
    }

    @Test
    fun polymorphicTypeIsEnum() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicTypeIsEnum",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicTypeIsEnum {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                " public enum InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@${ElementNameMatcher::class.simpleName} only allows classes. test.PolymorphicTypeIsEnum.InnerClass is a not a class!")
    }

    @Test
    fun polymorphicTypeIsNotSubType() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicTypeIsNotSubType",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicTypeIsNotSubType {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass.class)",
                "    )",
                "   String aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type test.PolymorphicTypeIsNotSubType.InnerClass must be a sub type of java.lang.String. Otherwise this type cannot be used in @${ElementNameMatcher::class.simpleName} to resolve polymorphis")
    }

    @Test
    fun polymorphicTypeIsSubType() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicTypeIsSubType",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicTypeIsSubType {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " static class InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicEmptyXmlName() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicEmptyXmlName",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicEmptyXmlName {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(name=\"\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " static class InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicBlankXmlName() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicBlankXmlName",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicBlankXmlName {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " static class InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicElementNameInConflict() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElementNameInConflict",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicElementNameInConflict {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass2.class),",
                "    })",
                "   Object aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass1 {}",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass2 {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: A @${ElementNameMatcher::class.simpleName} with the name \"foo\" is already mapped to the type test.PolymorphicElementNameInConflict.InnerClass1 to resolve polymorphism. Hence it cannot be mapped to test.PolymorphicElementNameInConflict.InnerClass2 as well.")
    }

    @Test
    fun polymorphicElementNoNamingConflict() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElementNoNamingConflict",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PolymorphicElementNoNamingConflict {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   Object aField;",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2 {}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun elementDeclarationOnPrimitive() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementDeclarationOnPrimitive",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementDeclarationOnPrimitive {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   int aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass1 {}",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass2 {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type of field 'aField' in class test.ElementDeclarationOnPrimitive is not a class nor a interface. Only classes or interfaces can be annotated with @${Element::class.simpleName} annotation")
    }

    @Test
    fun elementOnInterfaceWithoutPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnInterfaceWithoutPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnInterfaceWithoutPolymorphism {",
                "   @${Element::class.java.canonicalName}",
                "   MyInterface aField;",
                "",
                " public interface MyInterface {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type of field 'aField' in class test.ElementOnInterfaceWithoutPolymorphism is an interface. Since interfaces cannot be instantiated you have to specify which class should be instantiated (resolve polymorphism) manually by @${Element::class.simpleName}( typesByElement = ... )")
    }

    @Test
    fun elementOnInterfaceWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnInterfaceWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   MyInterface aField;",
                "}",
                "",
                "interface MyInterface {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 implements MyInterface{}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2 implements MyInterface{}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    fun elementOnInterfaceWithPolymorphismWrong() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnInterfaceWithoutPolymorphismWrong",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnInterfaceWithoutPolymorphismWrong {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   MyInterface aField;",
                "",
                " public interface MyInterface {}",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass1 implements MyInterface{}",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass2 {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type test.ElementOnInterfaceWithoutPolymorphismWrong.InnerClass2 must be a sub type of test.ElementOnInterfaceWithoutPolymorphismWrong.MyInterface. Otherwise this type cannot be used in @${ElementNameMatcher::class.simpleName} to resolve polymorphism")

    }

    @Test
    fun elementOnAbstractClassWithoutPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnAbstractClassWithoutPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnAbstractClassWithoutPolymorphism {",
                "   @${Element::class.java.canonicalName}",
                "   MyClass aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " public abstract class MyClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type of field 'aField' in class test.ElementOnAbstractClassWithoutPolymorphism is an abstract class. Since abstract classes cannot no be instantiated you have to specify which class should be instantiated (resolve polymorphism) manually by @${Element::class.simpleName}( typesByElement = ... )")
    }

    @Test
    fun elementOnAbstractClassWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnAbstractClassWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   MyClass aField;",
                "}",
                "",
                "abstract class MyClass {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 extends MyClass{}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2 extends MyClass{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    fun elementOnAbstractWithPolymorphismWrong() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnAbstractClassWithPolymorphismWrong",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnAbstractClassWithPolymorphismWrong {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   MyClass aField;",
                "",
                "}",
                "",
                "abstract class MyClass {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 extends MyClass{}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2 {}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type test.InnerClass2 must be a sub type of test.MyClass. Otherwise this type cannot be used in @${ElementNameMatcher::class.simpleName} to resolve polymorphism")
    }

    @Test
    fun elementListAbstractClassWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnAbstractClassWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   java.util.List<MyClass> aField;",
                "}",

                "",
                "abstract class MyClass {}",
                "@${Xml::class.java.canonicalName}",
                " class InnerClass1 extends MyClass{}",
                "@${Xml::class.java.canonicalName}",
                " class InnerClass2 extends MyClass{}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    fun elementListInterfaceWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementOnAbstractClassWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementOnInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   java.util.List<MyInterface> aField;",
                "}",
                "",
                " interface MyInterface {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 implements MyInterface{}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2 implements MyInterface{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    fun elementListRawAbstractClassWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListRawAbstractClassWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListRawAbstractClassWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   java.util.List aField;",
                "}",
                "",
                "abstract class MyClass {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 extends MyClass{}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2 extends MyClass{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    fun elementListRawInterfaceWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListRawInterfaceWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListRawInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   java.util.List aField;",
                "}",
                "",
                " interface MyInterface {}",
                "@${Xml::class.java.canonicalName}",
                " class InnerClass1 implements MyInterface{}",
                "@${Xml::class.java.canonicalName}",
                " class InnerClass2 implements MyInterface{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    @Ignore
    fun elementListWildcardExtendsInterfaceWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWildcardInterfaceWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWildcardInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   java.util.List<? extends MyInterface> aField;",
                "",
                " public interface MyInterface {}",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass1 implements MyInterface{}",
                "@${Xml::class.java.canonicalName}",
                " public class InnerClass2 implements MyInterface{}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    fun elementListWildcardExtendsInterfaceWithPolymorphismWrong() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWildcardExtendsInterfaceWithPolymorphismWrong",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWildcardExtendsInterfaceWithPolymorphismWrong {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   java.util.List<? extends MyInterface> aField;",
                "}",
                "",
                " interface MyInterface {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 implements MyInterface{}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type test.InnerClass2 must be a sub type of test.MyInterface. Otherwise this type cannot be used in @${ElementNameMatcher::class.simpleName} to resolve polymorphism")

    }

    @Test
    fun elementListWildcardSuperInterfaceWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWildcardInterfaceWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWildcardInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=GrandParent.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=Parent.class),",
                "    })",
                "   java.util.List<? super GrandParent> aField;",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class GrandParent {}",
                "@${Xml::class.java.canonicalName}",
                "class Parent extends GrandParent {}",
                "@${Xml::class.java.canonicalName}",
                "class Child extends Parent {}"
        )


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()

    }

    @Test
    fun elementListWildcardSuperInterfaceWithPolymorphismWrong() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWildcardSuperInterfaceWithPolymorphismWrong",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWildcardSuperInterfaceWithPolymorphismWrong {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=GrandParent.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=Parent.class),",
                "    })",
                "   java.util.List<? super GrandParent> aField;",
                "",
                "@${Xml::class.java.canonicalName}",
                " public class GrandParent {}",
                "@${Xml::class.java.canonicalName}",
                " public class Parent {}",
                "@${Xml::class.java.canonicalName}",
                " public class Child extends Parent {}",
                "}")


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type test.ElementListWildcardSuperInterfaceWithPolymorphismWrong.Parent must be a sub type of test.ElementListWildcardSuperInterfaceWithPolymorphismWrong.GrandParent. Otherwise this type cannot be used in @${ElementNameMatcher::class.simpleName} to resolve polymorphism")

    }

    @Test
    @Ignore
    fun elementListWildcardInterfaceWithoutPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWildcardInterfaceWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWildcardInterfaceWithPolymorphism {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   java.util.List<?> aField;",
                "}",
                "",
                "interface MyInterface {}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass1 implements MyInterface{}",
                "@${Xml::class.java.canonicalName}",
                "class InnerClass2 {}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun noConflictBetweenAttributeAndPropertyElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflict1",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflict1 {",
                "   @${PropertyElement::class.java.canonicalName}( name =\"foo\" )",
                "   int a;",
                "",
                "   @${Attribute::class.java.canonicalName}( name =\"foo\" )",
                "   int b;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun nameConflict() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflict2",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflict2 {",
                "   @${PropertyElement::class.java.canonicalName}( name =\"foo\" )",
                "   int a;",
                "",
                "   @${Element::class.java.canonicalName}( name =\"foo\" )",
                "   Other b;",
                "   @${Xml::class.java.canonicalName}",
                "   static class Other {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'b' in class test.NameConflict2 is in conflict with field 'a' in class test.NameConflict2. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun nameConflict2() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflict2",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflict2 {",
                "   @${Element::class.java.canonicalName}( name =\"foo\" )",
                "   Other b;",
                "   @${Xml::class.java.canonicalName}",
                "   public class Other {}",
                "",
                "   @${PropertyElement::class.java.canonicalName}( name =\"foo\" )",
                "   int a;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'a' in class test.NameConflict2 is in conflict with field 'b' in class test.NameConflict2. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun nameConflict3() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflict2",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflict2 {",
                "   @${Element::class.java.canonicalName}( name =\"foo\" )",
                "   Other b;",
                "",
                "   @${Element::class.java.canonicalName}( name =\"foo\" )",
                "   Other a;",
                "",
                "   @${Xml::class.java.canonicalName}",
                "   public class Other {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'a' in class test.NameConflict2 is in conflict with field 'b' in class test.NameConflict2. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun nameConflict4() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflict2",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflict2 {",
                "   @${Element::class.java.canonicalName}( name =\"foo\" )",
                "   Other a;",
                "",
                "   @${Element::class.java.canonicalName}( name =\"foo\" )",
                "   Other b;",
                "",
                "   @${Xml::class.java.canonicalName}",
                "   public class Other {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'b' in class test.NameConflict2 is in conflict with field 'a' in class test.NameConflict2. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun nameConflictInheritance1() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflictInheritance1",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflictInheritance1 extends Parent {",
                "   @${Attribute::class.java.canonicalName}( name =\"foo\" )",
                "   int a;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class Parent {",
                "   @${Attribute::class.java.canonicalName}( name =\"foo\" )",
                "   Other b;",
                "   class Other {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'b' in class test.Parent has the same xml attribute name 'foo' as the field 'a' in class test.NameConflictInheritance1. You can specify another name via annotations.")
    }

    @Test
    fun nameConflictInheritance2() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflictInheritance2",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflictInheritance2 extends Parent {",
                "   @${PropertyElement::class.java.canonicalName}( name =\"foo\" )",
                "   int a;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class Parent {",
                "   @${Element::class.java.canonicalName}( name =\"foo\" )",
                "   Other b;",
                "   @${Xml::class.java.canonicalName}",
                "   class Other {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'b' in class test.Parent is in conflict with field 'a' in class test.NameConflictInheritance2. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun attributeNotInConflictWithPropertyElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflictInheritance3",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class NameConflictInheritance3 extends Parent {",
                "   @${Attribute::class.java.canonicalName}( name =\"foo\" )",
                "   int a;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class Parent {",
                "   @${PropertyElement::class.java.canonicalName}( name =\"foo\" )",
                "   String b;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun nameConflictInheritanceOff() {
        val componentFile = JavaFileObjects.forSourceLines("test.NameConflictInheritance3",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(inheritance = false)",
                "class NameConflictInheritance3 extends Parent {",
                "   @${Attribute::class.java.canonicalName}( name =\"foo\" )",
                "   int a;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class Parent {",
                "   @${PropertyElement::class.java.canonicalName}( name =\"foo\" )",
                "   String b;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun textContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContent {",
                "   @${TextContent::class.qualifiedName}",
                "   String foo;",
                "",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun multipleTextContentInInheritance() {
        val componentFile = JavaFileObjects.forSourceLines("test.MultipleTextContentOnInheritance",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class MultipleTextContentOnInheritance extends Parent {",
                "   @${TextContent::class.qualifiedName}",
                "   String foo;",
                "",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class Parent {",
                "   @${TextContent::class.qualifiedName}",
                "   String bar;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun textContentOnNotStringField() {
        val componentFile = JavaFileObjects.forSourceLines("test.TextContentOnNotString",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class TextContentOnNotString {",
                "   @${TextContent::class.qualifiedName}",
                "   int foo;",
                "",
                "}"
        )


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Only type String is supported for @TextContent but field 'foo' in class test.TextContentOnNotString is not of type String")
    }

    @Test
    fun incorrectPathAnnotation() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"\")",
                "   @${Attribute::class.qualifiedName}",
                "   int foo;",
                "",
                "}"
        )


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'foo' in class test.PathAnnotation annotated with @Path(\"\") has an illegal path: Error in path segment '' (segment index = 0)")
    }

    @Test
    fun incorrectPathAnnotation2() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"asd/\")",
                "   @${Attribute::class.qualifiedName}",
                "   int foo;",
                "",
                "}"
        )


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'foo' in class test.PathAnnotation annotated with @Path(\"asd/\") has an illegal path: Error in path segment '' (segment index = 1)")
    }

    @Test
    fun incorrectPathAnnotation3() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"asd/foo/\")",
                "   @${Attribute::class.qualifiedName}",
                "   int foo;",
                "",
                "}"
        )


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'foo' in class test.PathAnnotation annotated with @Path(\"asd/foo/\") has an illegal path: Error in path segment '' (segment index = 2)")
    }

    @Test
    fun incorrectPathAnnotation4() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"asd/f oo\")",
                "   @${Attribute::class.qualifiedName}",
                "   int foo;",
                "",
                "}"
        )


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The field 'foo' in class test.PathAnnotation annotated with @Path(\"asd/f oo\") has an illegal path: Error in path segment 'f oo' (segment index = 1)")
    }

    @Test
    fun correctPathAnnotation() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"foo/bar\")",
                "   @${Attribute::class.qualifiedName}",
                "   int foo;",
                "",
                "}"
        )


        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun attributeAndPathConflict0() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"foo\")",
                "   @${Attribute::class.qualifiedName}",
                "   int bar;",
                "",
                "   @${Element::class.qualifiedName}",
                "   foo foo;",
                "",
                "   @${Xml::class.java.canonicalName}",
                "   static class foo {}",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'foo' in class test.PathAnnotation is in conflict with test.PathAnnotation. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun attributeAndPathConflict1() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"foo\")",
                "   @${Attribute::class.qualifiedName}",
                "   int bar;",
                "",
                "   @${Element::class.qualifiedName}",
                "   Foo foo;",
                "",
                "   @${Xml::class.java.canonicalName}",
                "   static class Foo {}",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'foo' in class test.PathAnnotation is in conflict with test.PathAnnotation. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun attributeAndPathConflict2() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"foo\")",
                "   @${Attribute::class.qualifiedName}",
                "   int bar;",
                "",
                "   @${Element::class.qualifiedName}",
                "   Other foo;",
                "",
                "   @${Xml::class.java.canonicalName}(name=\"foo\")",
                "   static class Other {}",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'foo' in class test.PathAnnotation is in conflict with test.PathAnnotation. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun attributeAndPathConflict3() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Element::class.qualifiedName}(name=\"foo\")",
                "   Other foo;",
                "",
                "   @${Path::class.qualifiedName}(\"foo\")",
                "   @${Attribute::class.qualifiedName}",
                "   int bar;",
                "",
                "   @${Xml::class.java.canonicalName}",
                "   public class Other {}",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Element field 'foo' in class test.PathAnnotation can't have attributes that are accessed from outside of the TypeAdapter that is generated from @${Element::class.simpleName} annotated class! Therefore attribute field 'bar' in class test.PathAnnotation can't be added. Most likely the @${Path::class.simpleName} is in conflict with an @${Element::class.simpleName} annotation.")
    }

    @Test
    fun attributeAndPathConflictWithSubPath() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"asd\")",
                "   @${Element::class.qualifiedName}",
                "   Other foo;",
                "",
                "   @${Path::class.qualifiedName}(\"asd/other\")",
                "   @${Attribute::class.qualifiedName}",
                "   int bar;",
                "",
                "   @${Xml::class.java.canonicalName}",
                "   public class Other {}",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Element field 'foo' in class test.PathAnnotation can't have attributes that are accessed from outside of the TypeAdapter that is generated from @${Element::class.simpleName} annotated class! Therefore attribute field 'bar' in class test.PathAnnotation can't be added. Most likely the @${Path::class.simpleName} is in conflict with an @${Element::class.simpleName} annotation.")
    }

    @Test
    fun attributeAndPathConflictWithSubPath2() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathAnnotation",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathAnnotation {",
                "   @${Path::class.qualifiedName}(\"asd/other\")",
                "   @${Attribute::class.qualifiedName}",
                "   int bar;",
                "",
                "   @${Path::class.qualifiedName}(\"asd\")",
                "   @${Element::class.qualifiedName}",
                "   Other foo;",
                "",
                "   @${Xml::class.java.canonicalName}",
                "   public class Other {}",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'foo' in class test.PathAnnotation is in conflict with test.PathAnnotation. Maybe both have the same xml name 'other' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
    }

    @Test
    fun elementListWithInterfaceAsGenericType() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWithInterface",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWithInterface {",

                "  @${Element::class.qualifiedName}",
                "  java.util.List<AnInterface> alist;",
                "}",
                "",
                "interface AnInterface{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The generic list type of 'alist' in class test.ElementListWithInterface is an interface. Hence polymorphism must be resolved manually by using @${ElementNameMatcher::class.simpleName}.")
    }

    @Test
    fun elementListWithAbstractClassAsGenericType() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWithAbstractClass",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWithAbstractClass {",

                "  @${Element::class.qualifiedName}",
                "  java.util.List<AbstractClass> alist;",
                "}",
                "",
                "abstract class AbstractClass{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The generic list type of 'alist' in class test.ElementListWithAbstractClass is a abstrac class. Hence polymorphism must be resolved manually by using @ElementNameMatcher.")
    }

    @Test
    fun elementListWithClassWithoutXmlAnnotationAsGenericType() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWithNotAnnotatedClass",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWithNotAnnotatedClass {",

                "  @${Element::class.qualifiedName}",
                "  java.util.List<OtherClass> alist;",
                "}",
                "",
                "class OtherClass{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The type test.OtherClass used for field 'alist' in test.ElementListWithNotAnnotatedClass can't be used, because is not annotated with @${Xml::class.simpleName}. Annotate test.OtherClass with @${Xml::class.simpleName}!")
    }

    @Test
    fun validElementList() {
        val componentFile = JavaFileObjects.forSourceLines("test.ValidElementList",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ValidElementList {",

                "  @${Element::class.qualifiedName}",
                "  java.util.List<OtherClass> alist;",
                "}",
                "",
                "@${Xml::class.java.canonicalName}",
                "class OtherClass{}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun failOnPathWithTextContent() {
        val componentFile = JavaFileObjects.forSourceLines("test.PathOnTextContent",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class PathOnTextContent {",

                "  @${Path::class.qualifiedName}(\"asd\")",
                "  @${TextContent::class.qualifiedName}",
                "  String foo;",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("@${Path::class.simpleName} on @${TextContent::class.simpleName} is not allowed. Use @${PropertyElement::class.simpleName} and @${Path::class.simpleName} instead on field 'foo' in class test.PathOnTextContent")
    }

    /**
     * Issue:
     * https://github.com/Tickaroo/tikxml/pull/66
     */
    @Test
    fun testEmptyConstructorOnElementNameMatcher() {
        val actionFile = JavaFileObjects.forSourceLines("test.complex.action.Action",
                "package test.complex.action;",
                "@${Xml::class.java.canonicalName}(name = \"action\")",
                "public class Action {",
                "  @${Attribute::class.qualifiedName}(name =\"action\")",
                "  public String action;",
                "}"
        )

        val testAction = JavaFileObjects.forSourceLines("test.complex.action.TestAction",
                "package test.complex.action;",
                "@${Xml::class.java.canonicalName}(name = \"testAction\")",
                "public class TestAction {",
                "}"
        )
        val filterA = JavaFileObjects.forSourceLines("test.complex.filter.action.FilterA",
                "package test.complex.filter.action;",
                "@${Xml::class.java.canonicalName}(name = \"filterA\")",
                "public class FilterA {",
                "}"
        )

        val filterB = JavaFileObjects.forSourceLines("test.complex.filter.action.FilterB",
                "package test.complex.filter.action;",
                "@${Xml::class.java.canonicalName}(name = \"filterB\")",
                "public class FilterB {",
                "  @${Attribute::class.qualifiedName}(name =\"target\")",
                "  public String target;",
                "}"
        )

        val filterC = JavaFileObjects.forSourceLines("test.complex.filter.action.FilterC",
                "package test.complex.filter.action;",
                "@${Xml::class.java.canonicalName}(name = \"filterC\")",
                "public class FilterC {",
                "}"
        )

        val filterD = JavaFileObjects.forSourceLines("test.complex.filter.action.FilterD",
                "package test.complex.filter.action;",
                "@${Xml::class.java.canonicalName}(name = \"filterD\")",
                "public class FilterD {",
                "}"
        )
        val filterE = JavaFileObjects.forSourceLines("test.complex.filter.action.FilterE",
                "package test.complex.filter.action;",
                "@${Xml::class.java.canonicalName}(name = \"filterE\")",
                "public class FilterE {",
                "}"
        )

        val filterFile = JavaFileObjects.forSourceLines("test.complex.filter.Filter",
                "package test.complex.filter;",
                "@${Xml::class.java.canonicalName}(name =\"filter\")",
                "public class Filter {",
                "  @${Path::class.qualifiedName}(\"action\")",
                "  @${Element::class.qualifiedName}( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}( name = \"filterA\", type= test.complex.filter.action.FilterA.class),",
                "     @${ElementNameMatcher::class.qualifiedName}( name = \"filterB\", type= test.complex.filter.action.FilterB.class),",
                "     @${ElementNameMatcher::class.qualifiedName}( name = \"filterC\", type= test.complex.filter.action.FilterC.class),",
                "     @${ElementNameMatcher::class.qualifiedName}( name = \"filterD\", type= test.complex.filter.action.FilterD.class),",
                "     @${ElementNameMatcher::class.qualifiedName}( name = \"filterE\", type= test.complex.filter.action.FilterE.class),",
                "  })",
                "  public Object action;",
                "}")

        val eventFile = JavaFileObjects.forSourceLines("test.complex.Event",
                "package test.complex;",
                "@${Xml::class.java.canonicalName}(name =\"event\")",
                "public class Event {",
                "  @${Path::class.qualifiedName}(\"action\")",
                "  @${Element::class.qualifiedName}( typesByElement = {",
                "     @${ElementNameMatcher::class.qualifiedName}( name = \"action\", type= test.complex.action.Action.class),",
                "     @${ElementNameMatcher::class.qualifiedName}( name = \"testAction\", type= test.complex.action.TestAction.class)",
                "  })",
                "  public Object action;",
                "  @${Element::class.qualifiedName}(name =\"filter\")",
                "  public test.complex.filter.Filter filter;",
                "}"
        )

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(arrayListOf<JavaFileObject>(
                        actionFile,
                        testAction,
                        filterA,
                        filterB,
                        filterC,
                        filterD,
                        filterE,
                        filterFile,
                        eventFile
                ))
                .processedWith(XmlProcessor())
                .compilesWithoutError()

    }

}
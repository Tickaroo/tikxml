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
 *
 * @author Hannes Dorfmann
 */
class AnnotationOnlyScanStrategyTest {


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
                "@${Xml::class.java.simpleName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
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
                "@${Xml::class.java.simpleName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
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
                "@${Xml::class.java.simpleName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
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
                "@${Xml::class.java.simpleName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
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
    fun inlineListOnNotListType() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnNotListType",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${InlineList::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class InlineListOnNotListType {",
                "   @${InlineList::class.java.simpleName}",
                "   @${Element::class.java.simpleName}",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The annotation @InlineList is only allowed on java.util.List types, but the field 'aField' in class test.InlineListOnNotListType is of type java.lang.String")
    }

    @Test
    fun inlineListOnNotListTypeWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnNotListTypeWithPolymorphism",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${InlineList::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class InlineListOnNotListTypeWithPolymorphism {",
                "   @${InlineList::class.java.simpleName}",
                "   @${Element::class.java.simpleName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=java.lang.Object)",
                "    )",
                "   String aField;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The annotation @InlineList is only allowed on java.util.List types, but the field 'aField' in class test.InlineListOnNotListTypeWithPolymorphism is of type java.lang.String")
    }

    @Test
    fun inlineListOnListType() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnListType",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${InlineList::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class InlineListOnListType {",
                "   @${InlineList::class.java.simpleName}",
                "   @${Element::class.java.simpleName}",
                "   java.util.List<String> aList;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun inlineListOnArrayListType() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnArrayListType",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class InlineListOnArrayListType {",
                "   @${InlineList::class.java.canonicalName}",
                "   @${Element::class.java.canonicalName}",
                "   java.util.ArrayList<String> aList;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun inlineListOnLinkedListType() {
        val componentFile = JavaFileObjects.forSourceLines("test.InlineListOnLinkedListType",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class InlineListOnLinkedListType {",
                "   @${InlineList::class.java.canonicalName}",
                "   @${Element::class.java.canonicalName}",
                "   java.util.LinkedList<String> aList;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicTypeIsPrivateClass() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicClassIsPrivate",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicClassIsPrivate {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerPrivateClass.class)",
                "    )",
                "   Object aField;",
                "",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicClassIsProtected {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerProtectedClass.class)",
                "    )",
                "   Object aField;",
                "",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicClassHasNoPublicConstructor {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicClassHasNoEmptyConstructor {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicTypeIsInterface {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass.class)",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicTypeIsEnum {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass.class)",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicTypeIsNotSubType {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass.class)",
                "    )",
                "   String aField;",
                "",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicTypeIsSubType {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                " public class InnerClass {}",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicEmptyXmlName {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"\" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                " public class InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The xml element name in @${ElementNameMatcher::class.simpleName} cannot be empty")
    }

    @Test
    fun polymorphicBlankXmlName() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicBlankXmlName",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicBlankXmlName {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = @${ElementNameMatcher::class.qualifiedName}(elementName=\"    \" , type=InnerClass.class)",
                "    )",
                "   Object aField;",
                "",
                " public class InnerClass {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The xml element name in @${ElementNameMatcher::class.simpleName} cannot be empty")
    }

    @Test
    fun polymorphicElementNameInConflict() {
        val componentFile = JavaFileObjects.forSourceLines("test.PolymorphicElementNameInConflict",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicElementNameInConflict {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass2.class),",
                "    })",
                "   Object aField;",
                "",
                " public class InnerClass1 {}",
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
                "@${Xml::class.java.canonicalName}(scanMode = ${ScanMode::class.qualifiedName}.${ScanMode.ANNOTATIONS_ONLY})",
                "class PolymorphicElementNoNamingConflict {",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(elementName=\"foo\" , type=InnerClass1.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(elementName=\"bar\" , type=InnerClass2.class),",
                "    })",
                "   Object aField;",
                "",
                " public class InnerClass1 {}",
                " public class InnerClass2 {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }
}
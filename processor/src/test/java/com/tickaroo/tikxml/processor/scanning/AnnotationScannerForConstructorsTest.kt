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
 * @author Hannes Dorfmann
 */
class AnnotationScannerForConstructorsTest {

    @Test
    fun allConstructorParamsAnnotatedPublicVisibility1() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { public AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int attribute) {} ",
                "    public int getAttribute(){return 0;}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun allConstructorParamsAnnotatedPublicVisibilityNoGetter() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { public AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int attribute) {} ",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The constructor parameter 'attribute' in constructor AnnotatedConstructorClass(int) in class test.AnnotatedConstructorClass  is annotated with a TikXml annotation. Therefore a getter method with minimum package visibility with the name getAttribute() or isAttribute() in case of a boolean must be provided. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun allConstructorParamsAnnotatedPackageVisibility1() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int attribute) {}",
                "    int getAttribute(){return 0;}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun allConstructorParamsAnnotatedPackageVisibilityPrivateGetter1() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int attribute) {}",
                "    private int getAttribute(){return 0;}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The constructor parameter 'attribute' in constructor AnnotatedConstructorClass(int) in class test.AnnotatedConstructorClass  is annotated with a TikXml annotation. Therefore a getter method with minimum package visibility with the name getAttribute() or isAttribute() in case of a boolean must be provided. Unfortunately, there is no such getter method. Please provide one!")
    }

    @Test
    fun allConstructorParamsAnnotatedPrivateVisibility1() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { private AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int attribute) {} }")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The constructor AnnotatedConstructorClass(int) in class test.AnnotatedConstructorClass is annotated with TikXml annotations and therefore must have at least package visibility to be able to be invoked from TikXml")
    }

    @Test
    fun notAllConstructorParamsAreAnnotated() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { private AnnotatedConstructorClass(int a, @${Attribute::class.java.simpleName} int attribute) {} }")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("The constructor AnnotatedConstructorClass(int,int) in class test.AnnotatedConstructorClass contains a mix of TikXml annotated parameters and not annotated parameters.That is not allowed! Either annotate all parameters or none of them")
    }

    @Test
    fun constructorsAndFieldsAnnotated1() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { public AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int attribute) {} ",
                "@${Attribute::class.java.simpleName} String name;",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("test.AnnotatedConstructorClass has TikXml annotated fields AND an annotated constructor AnnotatedConstructorClass(int) . That is not allowed! Either annotate fields or a constructor (but not a mix of both)")
    }

    @Test
    fun twoAnnotatedConstructors() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { ",
                "    public AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int other) {} ",
                "    public AnnotatedConstructorClass(@${Attribute::class.java.simpleName} String foo) {} ",
                "    public String getFoo(){ return null; } ",
                "    public int getOther(){ return 0; } ",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Only one constructor with TikXml annotated parameters is allowed but found multiple constructors with annotated parameters in class test.AnnotatedConstructorClass:    1) AnnotatedConstructorClass(int)    2) AnnotatedConstructorClass(java.lang.String)")
    }

    @Test
    fun multipleConstructorsButOnlyOneAnnotated() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { ",
                "    public AnnotatedConstructorClass(@${Attribute::class.java.simpleName} int other) {} ",
                "    public AnnotatedConstructorClass(String foo) {} ",
                "    public AnnotatedConstructorClass() {} ",
                "    public int getOther(){ return 0; } ",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun constructorsAndFieldsAnnotated2() {
        val componentFile = JavaFileObjects.forSourceLines("test.ItemConstructor",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@Xml",
                "public class ItemConstructor {",
                "    @Attribute String aString;",
                "    @Attribute int anInt;",
                "   @Attribute boolean aBoolean;",
                "    @Attribute double aDouble;",
                "    @Attribute long aLong;",
                "    @Attribute Integer intWrapper;",
                "    @Attribute Boolean booleanWrapper;",
                "    @Attribute Double doubleWrapper;",
                "    @Attribute Long longWrapper;",
                "",
                "    public ItemConstructor(@Attribute String aString, @Attribute int anInt, @Attribute boolean aBoolean, @Attribute double aDouble, @Attribute long aLong, @Attribute Integer intWrapper, @Attribute Boolean booleanWrapper, @Attribute Double doubleWrapper, @Attribute Long longWrapper) {",
                "        this.aString = aString;",
                "        this.anInt = anInt;",
                "        this.aBoolean = aBoolean;",
                "        this.aDouble = aDouble;",
                "       this.aLong = aLong;",
                "       this.intWrapper = intWrapper;",
                "        this.booleanWrapper = booleanWrapper;",
                "       this.doubleWrapper = doubleWrapper;",
                "        this.longWrapper = longWrapper;",
                "    }",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .failsToCompile()
                .withErrorContaining("Conflict: field 'aString' in class test.ItemConstructor has the same xml attribute name 'aString' as the field 'aString' in class test.ItemConstructor. You can specify another name via annotations.")
    }

    @Test
    fun booleanConstructorParameter1() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { AnnotatedConstructorClass(@${Attribute::class.java.simpleName} boolean attribute) {}",
                "    boolean isAttribute(){return false;}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun booleanConstructorParameterWithIsPrefixInParameterName() {
        val componentFile = JavaFileObjects.forSourceLines("test.NoConstructorClass",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "class AnnotatedConstructorClass { AnnotatedConstructorClass(@${Attribute::class.java.simpleName} boolean isAttribute) {}",
                "    boolean isAttribute(){return false;}",
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
                "    public AnnotatedConstructorClass(@${TextContent::class.java.simpleName} String someContent){}",
                "    public String getSomeContent(){return null;}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }

    @Test
    fun elementListWildcardSuperInterfaceWithPolymorphism() {
        val componentFile = JavaFileObjects.forSourceLines("test.ElementListWildcardInterfaceWithPolymorphism",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class ElementListWildcardInterfaceWithPolymorphism {",
                "   public ElementListWildcardInterfaceWithPolymorphism(",
                "   @${Element::class.java.canonicalName}(",
                "       typesByElement = {",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"foo\" , type=GrandParent.class),",
                "       @${ElementNameMatcher::class.qualifiedName}(name=\"bar\" , type=Parent.class),",
                "    })",
                "   java.util.List<? super GrandParent> aList) {}",
                "   public java.util.List<? super GrandParent> getAList(){return null;}",
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
}
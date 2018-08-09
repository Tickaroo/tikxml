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

package com.tickaroo.tikxml.autovalue

import com.google.auto.value.AutoValue
import com.google.auto.value.processor.AutoValueProcessor
import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory
import com.google.testing.compile.JavaSourcesSubject
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.annotation.Xml
import org.junit.Test
import javax.tools.JavaFileObject

/**
 *
 * @author Hannes Dorfmann
 */
class ElementTest {

    @Test
    fun simpleElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.A",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class A {",
                "   @${Element::class.qualifiedName} public abstract B someA();",
                "}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class B {}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .compilesWithoutError()
    }

    @Test
    fun listElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.A",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "import java.util.List;",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class A {",
                "   @${Element::class.qualifiedName} public abstract List<B> listOfB();",
                "}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class B {}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .compilesWithoutError()
    }


    @Test
    fun polymorphicElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.Root",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "import ${ElementNameMatcher::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class Root {",
                "   @${Element::class.qualifiedName}(typesByElement={@ElementNameMatcher(name = \"foo\", type = A.class), @ElementNameMatcher(type = B.class) }) public abstract I someI();",
                "}",
                "",
                "interface I{}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class A implements I{}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class B implements I {}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .compilesWithoutError()
    }

    @Test
    fun polymorphicListElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.Root",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "import ${ElementNameMatcher::class.java.canonicalName};",
                "import ${Element::class.java.canonicalName};",
                "import ${Attribute::class.java.canonicalName};",
                "import java.util.List;",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class Root {",
                "   @${Element::class.qualifiedName}(typesByElement={@ElementNameMatcher(type = A.class), @ElementNameMatcher(type = B.class) }) public abstract List<I> listOfI();",
                "}",
                "",
                "interface I{}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class A implements I{",
                "     @Attribute public abstract String aString();",
                "}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class B implements I {",
                "     @Element public abstract A a();",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .compilesWithoutError()
    }


    @Test
    fun simpleElementWithMultipleNamespaces() {
        val componentFile = JavaFileObjects.forSourceLines("test.A",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}( ",
                        "    writeNamespaces = {\"a=http://www.w3.org/2005/Atom\", \"b=http://www.w3.org/2005/Atom2\"}",
                        ")",
                "@${AutoValue::class.java.simpleName}",
                "abstract class A {",
                "   @${Element::class.qualifiedName} public abstract B someA();",
                "}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class B {}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .compilesWithoutError()
    }



    @Test
    fun simpleElementWithNamespace() {
        val componentFile = JavaFileObjects.forSourceLines("test.A",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}( ",
                "    writeNamespaces = {\"a=http://www.w3.org/2005/Atom\"}",
                ")",
                "@${AutoValue::class.java.simpleName}",
                "abstract class A {",
                "   @${Element::class.qualifiedName} public abstract B someA();",
                "}",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class B {}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .compilesWithoutError()
    }
}
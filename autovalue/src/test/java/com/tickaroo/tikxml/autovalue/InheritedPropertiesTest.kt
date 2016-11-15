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
import com.tickaroo.tikxml.annotation.*
import org.junit.Ignore
import org.junit.Test
import javax.tools.JavaFileObject

/**
 *
 * @author Hannes Dorfmann
 */
class InheritedPropertiesTest {

    @Test
    fun notAnnotatedPropertiesFromInheritedInterface() {
        val componentFile = JavaFileObjects.forSourceLines("test.IgnoreProperties",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class IgnoreProperties implements FooInterface {",
                "   @${Attribute::class.qualifiedName} public abstract int anInt();",
                "}",
                "",
                "interface FooInterface {",
                "    String aProperty();",
                "    int anotherProperty();",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .failsToCompile()
                .withErrorContaining("class test.IgnoreProperties must have all methods (auto value properties methods) annotated with TikXml annotations like @Attribute, @${PropertyElement::class.simpleName}, @${Element::class.simpleName} or @${TextContent::class.simpleName}. It's not allowed to annotate just some of the property methods (incl. implemented interface methods that are also auto value property methods).")
    }

    @Test
    @Ignore
    fun ignoreParcelableProperties() {
        val componentFile = JavaFileObjects.forSourceLines("android.os.Parcelable",
                "package android.os;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class Foo implements android.os.Parcelable {",
                "   @${Attribute::class.qualifiedName} public abstract int anInt();",
                "}",
                "",
                "public interface Parcelable { ",
                "      public static final int PARCELABLE_WRITE_RETURN_VALUE = 0x0001;",
                "      public static final int CONTENTS_FILE_DESCRIPTOR = 0x0001;",
                "      public int describeContents ();",
                "      public void writeToParcel(Parcel dest, int flags);",
                "",
                " public interface Creator<T> {",
                "     public T createFromParcel(Parcel source);",
                "     public T[] newArray(int size);",
                "}",
                "",
                "public interface ClassLoaderCreator < T > extends Creator<T>",
                "{",
                "public T createFromParcel(Parcel source, ClassLoader loader);",
                "}",
                "}",
                "",
                "class Parcel{",
                " public final void writeInt(int val) {}",
                "}")

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile)
                .processedWith(AutoValueProcessor())
                .compilesWithoutError()

    }
}
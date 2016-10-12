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
import com.tickaroo.tikxml.TypeConverter
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.XmlProcessor
import org.junit.Test
import java.util.*
import javax.tools.JavaFileObject

/**
 * @author Hannes Dorfmann
 */
class CustomTypeConverterTest {

    @Test
    fun customAttributeTypeConverter() {
        val componentFile = JavaFileObjects.forSourceLines("test.MyDateConverter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class CustomTypeAdapterOnAttribute {",
                "   @${Attribute::class.qualifiedName}",
                "   ${Date::class.qualifiedName} foo;",
                "}",
                "",
                "public class MyDateConverter implements ${TypeConverter::class.qualifiedName}<${Date::class.qualifiedName}>{",
                "public ${Date::class.qualifiedName} read(String value) throws Exception{ return null; }",
                "public String write(${Date::class.qualifiedName} value){ return null; }",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }


    @Test
    fun propertyTypeConverter() {
        val componentFile = JavaFileObjects.forSourceLines("test.MyDateConverter",
                "package test;",
                "",
                "@${Xml::class.java.canonicalName}",
                "class CustomTypeAdapterOnAttribute {",
                "   @${PropertyElement::class.qualifiedName}",
                "   ${Date::class.qualifiedName} foo;",
                "}",
                "",
                "public class MyDateConverter implements ${TypeConverter::class.qualifiedName}<${Date::class.qualifiedName}>{",
                "public ${Date::class.qualifiedName} read(String value) throws Exception{ return null; }",
                "public String write(${Date::class.qualifiedName} value){ return null; }",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(XmlProcessor())
                .compilesWithoutError()
    }
}

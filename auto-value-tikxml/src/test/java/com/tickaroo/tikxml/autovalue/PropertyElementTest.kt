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
import com.tickaroo.tikxml.TypeConverter
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import org.junit.Test
import java.util.*
import javax.tools.JavaFileObject

/**
 *
 * @author Hannes Dorfmann
 */
class PropertyElementTest {

    @Test
    fun propertyElement() {
        val componentFile = JavaFileObjects.forSourceLines("test.DateConverter",
                "package test;",
                "",
                "import ${Xml::class.java.canonicalName};",
                "import ${AutoValue::class.java.canonicalName};",
                "import ${Date::class.java.canonicalName};",
                "import ${TypeConverter::class.java.canonicalName};",
                "",
                "@${Xml::class.java.simpleName}",
                "@${AutoValue::class.java.simpleName}",
                "abstract class A {",
                "   @${PropertyElement::class.qualifiedName} public abstract int someInt();",
                "   @${PropertyElement::class.qualifiedName} public abstract boolean someBoolean();",
                "   @${PropertyElement::class.qualifiedName} public abstract double someDouble();",
                "   @${PropertyElement::class.qualifiedName} public abstract long someLong();",
                "   @${PropertyElement::class.qualifiedName}(converter=DateConverter.class) public abstract Date someDate();",
                "}",
                "public class DateConverter implements TypeConverter<Date> {",
                "@Override public Date read(String value) throws Exception {",
                "    return null;",
                "}",
                "",
                "@Override public String write(Date value) throws Exception {",
                "    return null;",
                "}",
                "}"
        )

        Truth.assertAbout<JavaSourcesSubject.SingleSourceAdapter, JavaFileObject>(JavaSourceSubjectFactory.javaSource())
                .that(componentFile).processedWith(AutoValueProcessor())
                .compilesWithoutError()
    }
}
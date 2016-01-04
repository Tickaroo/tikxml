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

package com.tickaroo.tikxml.processor.converter

import com.tickaroo.tikxml.TypeConverter
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.processor.ProcessingException
import org.junit.Test
import org.mockito.Mockito
import javax.lang.model.element.VariableElement
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

/**
 *
 * @author Hannes Dorfmann
 */
class ConverterCheckerByClassTest {

    @Test
    fun interfaceConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        Mockito.doReturn(InterfaceTypeConverter::class.java).`when`(annotation).converter

        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter class com.tickaroo.tikxml.processor.converter.InterfaceTypeConverter must be a public class!", e.message)
        }
    }

    @Test
    fun abstractClassConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        Mockito.doReturn(AbstractTypeConverter::class.java).`when`(annotation).converter

        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter class com.tickaroo.tikxml.processor.converter.AbstractTypeConverter must be a public class!", e.message)
        }
    }

    @Test
    fun defaultVisibiltiyConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        Mockito.doReturn(DefaultVisibilityTypeConverter::class.java).`when`(annotation).converter

        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter class com.tickaroo.tikxml.processor.converter.DefaultVisibilityTypeConverter must be a public class!", e.message)
        }
    }

    @Test
    fun privateVisibiltiyConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        Mockito.doReturn(PrivateVisibilityTypeConverter::class.java).`when`(annotation).converter

        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter class com.tickaroo.tikxml.processor.converter.ConverterCheckerByClassTest.PrivateVisibilityTypeConverter must be a public class!", e.message)
        }
    }

    @Test
    fun onlyPrivateConstructorConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        Mockito.doReturn(PrivateConstructorTypeConverter::class.java).`when`(annotation).converter

        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter class com.tickaroo.tikxml.processor.converter.PrivateConstructorTypeConverter must provide an empty (parameter-less) public constructor", e.message)
        }
    }

    @Test
    fun noEmptyConstructorConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        Mockito.doReturn(NoParameterLessConstructorTypeConverter::class.java).`when`(annotation).converter

        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter class com.tickaroo.tikxml.processor.converter.NoParameterLessConstructorTypeConverter must provide an empty (parameter-less) public constructor", e.message)
        }
    }

    @Test
    fun noConverterShouldBeUsedViaAttributeAnnotation() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        Mockito.doReturn(TypeConverter.NoneTypeConverter::class.java).`when`(annotation).converter

        assertNull(attributeConverterChecker.getQualifiedConverterName(element, annotation))
    }

    @Test
    fun noConverterShouldBeUsedViaPropertyElementAnnotation() {
        val attributeConverterChecker = PropertyElementConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(PropertyElement::class.java)

        Mockito.doReturn(TypeConverter.NoneTypeConverter::class.java).`when`(annotation).converter

        assertNull(attributeConverterChecker.getQualifiedConverterName(element, annotation))
    }

    private class PrivateVisibilityTypeConverter : TypeConverter<Any> {

        @Throws(Exception::class)
        override fun read(value: String): Any? {
            return null
        }

        @Throws(Exception::class)
        override fun write(value: Any): String? {
            return null
        }
    }
}
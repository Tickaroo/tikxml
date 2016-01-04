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
import com.tickaroo.tikxml.processor.ProcessingException
import org.junit.Test
import org.mockito.Mockito
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import kotlin.collections.arrayListOf
import kotlin.collections.hashSetOf
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 *
 * @author Hannes Dorfmann
 */
class ConverterCheckerByTypeMirrorTest {


    @Test
    fun interfaceConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        for (kind in TypeKind.values()) {

            if (kind == TypeKind.DECLARED)
                continue // Skip declared, because declared is the expected behaviour

            val typeMirror = Mockito.mock(TypeMirror::class.java)
            Mockito.doReturn(kind).`when`(typeMirror).kind



            Mockito.doAnswer {
                throw MirroredTypeException(typeMirror)
            }.`when`(annotation).converter

            try {
                attributeConverterChecker.getQualifiedConverterName(element, annotation)
                fail("Processing Exception expected for type $kind")
            } catch(e: ProcessingException) {
                assertEquals("TypeConverter must be a class", e.message)
            }
        }
    }

    @Test
    fun notClassElement() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        for (kind in ElementKind.values()) {
            if (kind == ElementKind.CLASS) {
                continue
            }

            // Element
            val typeElement = Mockito.mock(TypeElement::class.java)
            Mockito.doReturn(kind).`when`(typeElement).kind
            Mockito.doReturn(AbstractTypeConverter::class.java.canonicalName).`when`(typeElement).toString()


            // Type Mirror
            val typeMirror = Mockito.mock(DeclaredType::class.java)
            Mockito.doReturn(TypeKind.DECLARED).`when`(typeMirror).kind
            Mockito.doReturn(typeElement).`when`(typeMirror).asElement()
            Mockito.doReturn(AbstractTypeConverter::class.java.canonicalName).`when`(typeMirror).toString()


            Mockito.doAnswer {
                throw MirroredTypeException(typeMirror)
            }.`when`(annotation).converter



            try {
                attributeConverterChecker.getQualifiedConverterName(element, annotation)
                fail("Processing Exception expected")
            } catch(e: ProcessingException) {
                assertEquals("TypeConverter com.tickaroo.tikxml.processor.converter.AbstractTypeConverter must be a public class!", e.message)
            }
        }
    }

    @Test
    fun abstractClassConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)


        // Element
        val typeElement = Mockito.mock(TypeElement::class.java)
        Mockito.doReturn(ElementKind.CLASS).`when`(typeElement).kind
        Mockito.doReturn(hashSetOf(Modifier.ABSTRACT)).`when`(typeElement).modifiers
        Mockito.doReturn(AbstractTypeConverter::class.java.canonicalName).`when`(typeElement).toString()



        // Type Mirror
        val typeMirror = Mockito.mock(DeclaredType::class.java)
        Mockito.doReturn(TypeKind.DECLARED).`when`(typeMirror).kind
        Mockito.doReturn(typeElement).`when`(typeMirror).asElement()
        Mockito.doReturn(AbstractTypeConverter::class.java.canonicalName).`when`(typeMirror).toString()

        Mockito.doAnswer {
            throw MirroredTypeException(typeMirror)
        }.`when`(annotation).converter


        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter com.tickaroo.tikxml.processor.converter.AbstractTypeConverter class is not public!", e.message)
        }

    }

    @Test
    fun defaultVisibiltiyConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)


        // Element
        val typeElement = Mockito.mock(TypeElement::class.java)
        Mockito.doReturn(ElementKind.CLASS).`when`(typeElement).kind
        Mockito.doReturn(hashSetOf(Modifier.STATIC)).`when`(typeElement).modifiers
        Mockito.doReturn(DefaultVisibilityTypeConverter::class.qualifiedName).`when`(typeElement).toString()

        // Type Mirror
        val typeMirror = Mockito.mock(DeclaredType::class.java)
        Mockito.doReturn(TypeKind.DECLARED).`when`(typeMirror).kind
        Mockito.doReturn(typeElement).`when`(typeMirror).asElement()

        Mockito.doAnswer {
            throw MirroredTypeException(typeMirror)
        }.`when`(annotation).converter


        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter com.tickaroo.tikxml.processor.converter.DefaultVisibilityTypeConverter class is not public!", e.message)
        }
    }

    @Test
    fun privateVisibiltiyConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)

        // Element
        val typeElement = Mockito.mock(TypeElement::class.java)
        Mockito.doReturn(ElementKind.CLASS).`when`(typeElement).kind
        Mockito.doReturn(hashSetOf(Modifier.PRIVATE)).`when`(typeElement).modifiers
        Mockito.doReturn(PrivateVisibilityTypeConverter::class.qualifiedName).`when`(typeElement).toString()

        // Type Mirror
        val typeMirror = Mockito.mock(DeclaredType::class.java)
        Mockito.doReturn(TypeKind.DECLARED).`when`(typeMirror).kind
        Mockito.doReturn(typeElement).`when`(typeMirror).asElement()

        Mockito.doAnswer {
            throw MirroredTypeException(typeMirror)
        }.`when`(annotation).converter


        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter com.tickaroo.tikxml.processor.converter.ConverterCheckerByTypeMirrorTest.PrivateVisibilityTypeConverter class is not public!", e.message)
        }
    }

    @Test
    fun onlyPrivateConstructorConverter() {
        val attributeConverterChecker = AttributeConverterChecker()

        val element = Mockito.mock(VariableElement::class.java)
        val annotation = Mockito.mock(Attribute::class.java)


        // Private Constructor
        val constructor = Mockito.mock(ExecutableElement::class.java)
        Mockito.doReturn(hashSetOf(Modifier.PRIVATE)).`when`(constructor).modifiers

        // Element
        val typeElement = Mockito.mock(TypeElement::class.java)
        Mockito.doReturn(ElementKind.CLASS).`when`(typeElement).kind
        Mockito.doReturn(hashSetOf(Modifier.PUBLIC)).`when`(typeElement).modifiers
        Mockito.doReturn(arrayListOf(constructor)).`when`(typeElement).enclosedElements

        // Type Mirror
        val typeMirror = Mockito.mock(DeclaredType::class.java)
        Mockito.doReturn(TypeKind.DECLARED).`when`(typeMirror).kind
        Mockito.doReturn(typeElement).`when`(typeMirror).asElement()
        Mockito.doReturn(PrivateConstructorTypeConverter::class.qualifiedName).`when`(typeMirror).toString()

        Mockito.doAnswer {
            throw MirroredTypeException(typeMirror)
        }.`when`(annotation).converter



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


        // Constructor
        val constructor = Mockito.mock(ExecutableElement::class.java)
        Mockito.doReturn(hashSetOf(Modifier.PUBLIC)).`when`(constructor).modifiers
        Mockito.doReturn(arrayListOf(Mockito.mock(VariableElement::class.java))).`when`(constructor).parameters

        // Element
        val typeElement = Mockito.mock(TypeElement::class.java)
        Mockito.doReturn(ElementKind.CLASS).`when`(typeElement).kind
        Mockito.doReturn(hashSetOf(Modifier.PUBLIC)).`when`(typeElement).modifiers
        Mockito.doReturn(arrayListOf(constructor)).`when`(typeElement).enclosedElements

        // Type Mirror
        val typeMirror = Mockito.mock(DeclaredType::class.java)
        Mockito.doReturn(TypeKind.DECLARED).`when`(typeMirror).kind
        Mockito.doReturn(typeElement).`when`(typeMirror).asElement()
        Mockito.doReturn(NoParameterLessConstructorTypeConverter::class.qualifiedName).`when`(typeMirror).toString()

        Mockito.doAnswer {
            throw MirroredTypeException(typeMirror)
        }.`when`(annotation).converter



        try {
            attributeConverterChecker.getQualifiedConverterName(element, annotation)
            fail("Processing Exception expected")
        } catch(e: ProcessingException) {
            assertEquals("TypeConverter class com.tickaroo.tikxml.processor.converter.NoParameterLessConstructorTypeConverter must provide an empty (parameter-less) public constructor", e.message)
        }
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
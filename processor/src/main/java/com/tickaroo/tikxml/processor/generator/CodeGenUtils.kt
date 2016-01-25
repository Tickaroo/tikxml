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

package com.tickaroo.tikxml.processor.generator

import com.squareup.javapoet.*
import com.tickaroo.tikxml.TikXmlConfig
import com.tickaroo.tikxml.TypeConverterNotFoundException
import com.tickaroo.tikxml.XmlReader
import com.tickaroo.tikxml.processor.field.access.FieldAccessPolicy
import com.tickaroo.tikxml.processor.utils.*
import com.tickaroo.tikxml.processor.xml.XmlElement
import com.tickaroo.tikxml.typeadapter.AttributeBinder
import com.tickaroo.tikxml.typeadapter.ChildElementBinder
import com.tickaroo.tikxml.typeadapter.NestedChildElementBinder
import java.io.IOException
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

/**
 *
 * @author Hannes Dorfmann
 */
class CodeGenUtils(val customTypeConverterManager: CustomTypeConverterManager, val typeConvertersForPrimitives: Set<String>, val  valueType: ClassName) {


    // Constants
    companion object PARAMS {
        const val valueParam = "value"
        const val tikConfigParam = "config"
        const val textContentParam = "textContent"
        const val readerParam = "reader"
        const val attributeBindersParam = "attributeBinders"
        const val childElementBindersParam = "childElementBinders"
    }


    /**
     * Get the parameterized (generics) type name for [AttributeBinder]
     */
    val attributeBinderType = ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), valueType)

    /**
     * Get the parameterized (generics) type name for [ChildElementBinder]
     */
    val childElementBinderType = ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), valueType)

    /**
     * Get the parameterized (generics) type name for [NestedChildElementBinder]
     */
    val nestedChildElementBinderType = ParameterizedTypeName.get(ClassName.get(NestedChildElementBinder::class.java), valueType)

    /**
     * Generate the attribute binders
     */
    fun generateAttributeBinders(currentElement: XmlElement): CodeBlock {

        // TODO optimize it for one single attribute

        val builder = CodeBlock.builder()
        for ((xmlElementName, attributeField) in currentElement.attributes) {

            val fromXmlMethodBuilder = fromXmlMethodBuilder()

            fromXmlMethodBuilder.addCode(assignViaTypeConverterOrPrimitive(attributeField.element, AssignmentType.ATTRIBUTE, attributeField.accessPolicy, attributeField.converterQualifiedName))


            val anonymousAttributeBinder = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), valueType))
                    .addMethod(fromXmlMethodBuilder.build())
                    .build()


            builder.addStatement("$attributeBindersParam.put(\$S, \$L)", xmlElementName, anonymousAttributeBinder)

        }

        return builder.build()
    }

    /**
     * get the assignment statement for reading attributes
     */
    fun assignViaTypeConverterOrPrimitive(element: Element, assignmentType: AssignmentType, accessPolicy: FieldAccessPolicy, customTypeConverterQualifiedClassName: String?): CodeBlock {

        val type = element.asType()
        val xmlReaderMethodPrefix = assignmentType.xmlReaderMethodPrefix()


        val surroundWithTryCatch = fun(assignmentStatement: String) = CodeBlock.builder()
                .beginControlFlow("try")
                .add(accessPolicy.resolveAssignment(assignmentStatement))
                .nextControlFlow("catch(\$T e)", ClassName.get(TypeConverterNotFoundException::class.java))
                .addStatement("throw e")
                .nextControlFlow("catch(\$T e)", ClassName.get(Exception::class.java))
                .addStatement("throw new \$T(e)", ClassName.get(IOException::class.java))
                .endControlFlow()
                .build()

        //
        // Use custom type converter?
        //
        if (customTypeConverterQualifiedClassName != null) {
            return surroundWithTryCatch("${customTypeConverterManager.getFieldNameForConverter(customTypeConverterQualifiedClassName)}.read($readerParam.$xmlReaderMethodPrefix())")
        }

        //
        // Primitives
        //
        if (type.isString()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.String.class).read($readerParam.$xmlReaderMethodPrefix())")

            }

            return accessPolicy.resolveAssignment("$readerParam.$xmlReaderMethodPrefix()")
        }

        if (type.isBoolean()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Boolean.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            if (typeConvertersForPrimitives.contains("boolean")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(boolean.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            return accessPolicy.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsBoolean()")
        }

        if (type.isDouble()) {

            if (typeConvertersForPrimitives.contains(Double::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Double.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("double")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(double.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return accessPolicy.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsDouble()")
        }

        if (type.isInt()) {

            if (typeConvertersForPrimitives.contains(Integer::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Integer.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("int")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(int.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return accessPolicy.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsInt()")
        }

        if (type.isLong()) {

            if (typeConvertersForPrimitives.contains(Long::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Long.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("long")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(long.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return accessPolicy.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsLong()")
        }

        //
        // Use typeconveter from TikConfig
        //
        return accessPolicy.resolveAssignment("$tikConfigParam.getTypeConverter($type.class).read($readerParam.${xmlReaderMethodPrefix}())")
    }

    /**
     * Generate a [NestedChildElementBinder] and recursively calls [com.tickaroo.tikxml.processor.xml.XmlChildElement] to generate its code
     */
    fun generateNestedChildElementBinder(element: XmlElement): TypeSpec {

        val initializerBuilder = CodeBlock.builder()
        if (element.hasAttributes()) {
            val attributeMapType = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), attributeBinderType)
            initializerBuilder.addStatement("$attributeBindersParam = new \$T()", attributeMapType);
            initializerBuilder.add(generateAttributeBinders(element))
        }

        if (element.hasChildElements()) {
            val childBinderTypeMap = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), childElementBinderType)
            initializerBuilder.addStatement("$childElementBindersParam = new \$T()", childBinderTypeMap);
            for ((xmlName, xmlElement) in element.childElements) {
                initializerBuilder.addStatement("${CodeGenUtils.childElementBindersParam}.put(\$S, \$L)", xmlName, xmlElement.generateReadXmlCode(this))
            }
        }


        // TODO text content?
        return TypeSpec.anonymousClassBuilder("false")
                .addSuperinterface(nestedChildElementBinderType)
                .addInitializerBlock(initializerBuilder.build())
                .build()
    }

    fun fromXmlMethodBuilder() = MethodSpec.methodBuilder("fromXml")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(XmlReader::class.java, readerParam)
            .addParameter(TikXmlConfig::class.java, tikConfigParam)
            .addParameter(valueType, valueParam)
            .addException(IOException::class.java)

    fun assignTextContentMethodBuilder() = MethodSpec.methodBuilder("assignTextContent")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PROTECTED)
            .addParameter(TikXmlConfig::class.java, tikConfigParam)
            .addParameter(String::class.java, textContentParam)
            .addParameter(valueType, valueParam)

    /**
     * Used to specify whether we are going to assign an xml attribute or an xml element text content
     */
    enum class AssignmentType {
        ATTRIBUTE,
        ELEMENT;

        fun xmlReaderMethodPrefix() = when (this) {
            ATTRIBUTE -> "nextAttributeValue"
            ELEMENT -> "nextTextContent"
        }
    }

}
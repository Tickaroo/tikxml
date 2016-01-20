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
import com.tickaroo.tikxml.processor.field.AnnotatedClass
import com.tickaroo.tikxml.processor.field.PropertyField
import com.tickaroo.tikxml.processor.utils.*
import com.tickaroo.tikxml.processor.xml.XmlElement
import com.tickaroo.tikxml.typeadapter.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements

/**
 * This class takes an [com.tickaroo.tikxml.processor.field.AnnotatedClass] as input
 * and generates a [com.tickaroo.tikxml.TypeAdapter] (java code) for it
 * @author Hannes Dorfmann
 * @since 1.0
 */
class TypeAdapterCodeGenerator(private val filer: Filer, private val elementUtils: Elements, private val typeConvertersForPrimitives: Set<String>) {

    private val converterManager = CustomTypeConverterManager();

    // Constants
    private val valueParam = "value"
    private val tikConfigParam = "config"
    private val textContentParam = "textContent"
    private val readerParam = "reader"
    private val attributeBindersParam = "attributeBinders"
    private val childElementBindersParam = "childElementBinders"

    /**
     * Generates an [com.tickaroo.tikxml.TypeAdapter] for the given class
     */
    fun generateCode(annotatedClass: AnnotatedClass) {

        val annotatedClassType = ClassName.get(annotatedClass.element)
        val genericParamTypeAdapter = ParameterizedTypeName.get(ClassName.get(DelegatingTypeAdapter::class.java), annotatedClassType)


        val assignTextContentBuilder = assignTextContentMethodBuilder(annotatedClassType)

        if (annotatedClass.textContentField != null) {
            val textContentElement = annotatedClass.textContentField!!.element
            assignTextContentBuilder.addStatement("$valueParam.${textContentElement.simpleName} = $textContentParam")
        }


        val newInstance = MethodSpec.methodBuilder("newInstance")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .returns(annotatedClassType)
                .addStatement("return new \$T()", annotatedClassType)
                .build()


        val constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super(${annotatedClass.textContentField != null})")
                .addCode(generateAttributeBinders(annotatedClassType, annotatedClass))


        val adapterClass = TypeSpec.classBuilder(annotatedClass.simpleClassName + TypeAdapter.GENERATED_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .superclass(genericParamTypeAdapter)
                .addMethod(constructorBuilder.build())
                .addMethod(newInstance)
                .addMethod(assignTextContentBuilder.build())
                .build()


        val packageElement = elementUtils.getPackageOf(annotatedClass.element)
        val packageName = if (packageElement.isUnnamed) "" else packageElement.toString()

        val javaFile = JavaFile.builder(packageName, adapterClass).build()
        javaFile.writeTo(filer)
    }

    /**
     * Generate the attribute binders
     */
    private fun generateAttributeBinders(valueType: ClassName, currentElement: XmlElement): CodeBlock {

        val builder = CodeBlock.builder()
        for ((xmlElementName, attributeField) in currentElement.attributes) {

            val fromXmlMethodBuilder = fromXmlMethodBuilder(valueType)

            fromXmlMethodBuilder.addCode(assignByReadingFromXmlReader(attributeField.element, AssignmentType.ATTRIBUTE, attributeField.converterQualifiedName))


            val anonymousAttributeBinder = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), valueType))
                    .addMethod(fromXmlMethodBuilder.build())
                    .build()


            builder.addStatement("$attributeBindersParam.put(\$S, \$N", xmlElementName, anonymousAttributeBinder)

        }

        return builder.build()
    }

    /**
     * get the assignment statement for reading attributes
     */
    private fun assignByReadingFromXmlReader(variableElement: VariableElement, assignmentType: AssignmentType, customTypeConverterQualifiedClassName: String?): CodeBlock {

        val type = variableElement.asType()
        val xmlReaderMethodPrefix = assignmentType.xmlReaderMethodPrefix()


        val assignment = fun(assignmentStatement: String) = CodeBlock.builder()
                .addStatement("$valueParam.$variableElement = $assignmentStatement")
                .build()


        val surroundWithTryCatch = fun(assignmentStatement: String) = CodeBlock.builder()
                .beginControlFlow("try")
                .add(assignment(assignmentStatement))
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
            return surroundWithTryCatch("${converterManager.getFieldNameForConverter(customTypeConverterQualifiedClassName)}.read($readerParam.$xmlReaderMethodPrefix())")
        }

        //
        // Primitives
        //
        if (type.isString()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.String.class).read($readerParam.$xmlReaderMethodPrefix())")

            }

            return assignment("$readerParam.$xmlReaderMethodPrefix()")
        }

        if (type.isBoolean()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Boolean.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            if (typeConvertersForPrimitives.contains("boolean")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(boolean.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            return assignment("$readerParam.${xmlReaderMethodPrefix}AsBoolean()")
        }

        if (type.isDouble()) {

            if (typeConvertersForPrimitives.contains(Double::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Double.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("double")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(double.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return assignment("$readerParam.${xmlReaderMethodPrefix}AsDouble()")
        }

        if (type.isInt()) {

            if (typeConvertersForPrimitives.contains(Integer::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Integer.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("int")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(int.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return assignment("$readerParam.${xmlReaderMethodPrefix}AsInt()")
        }

        if (type.isLong()) {

            if (typeConvertersForPrimitives.contains(Long::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Long.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("long")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(long.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return assignment("$readerParam.${xmlReaderMethodPrefix}AsLong()")
        }

        //
        // Use typeconveter from TikConfig
        //
        return assignment("$tikConfigParam.getTypeConverter($type.class).read($readerParam.${xmlReaderMethodPrefix}())")
    }

    private fun generateChildBinders(valueType: ClassName, currentElement: XmlElement) {

        for ((xmlName, xmlElement) in currentElement.childElements) {

            if (xmlElement.childElements.size > 2 ) {
                generateNestedChildElementBinder(valueType, currentElement)
            }

        }
    }

    /**
     * Generate a simple [ChildElementBinder] as anonymous instance
     */
    private fun generateChildElementBinder(valueType: ClassName, element: XmlElement): TypeSpec {


        val fromXmlMethod = fromXmlMethodBuilder(valueType)

        if (element is PropertyField) {
            fromXmlMethod.addCode(assignByReadingFromXmlReader(element.element, AssignmentType.ELEMENT, element.converterQualifiedName))
        }



        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), valueType))
                .addMethod(fromXmlMethod.build())
                .build()

    }

    private fun generateNestedChildElementBinder(valueType: ClassName, element: XmlElement): TypeSpec {

        val initializerBuilder = CodeBlock.builder()
        if (element.hasAttributes()) {
            val attributeBinderType = ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), valueType)
            val attributeMapType = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), attributeBinderType)
            initializerBuilder.addStatement("$attributeBindersParam = new \$T()", attributeMapType);
            initializerBuilder.add(generateAttributeBinders(valueType, element))
        }

        if (element.hasChildElements()) {
            val childBinderType = ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), valueType)
            val childBinderTypeMap = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), childBinderType)
            initializerBuilder.addStatement("$childElementBindersParam = new \$T()", childBinderTypeMap);
        }


        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(NestedChildElementBinder::class.java), valueType))
                .addInitializerBlock(initializerBuilder.build())
                .build()
    }

    private fun fromXmlMethodBuilder(valueType: ClassName) = MethodSpec.methodBuilder("fromXml")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(XmlReader::class.java, readerParam)
            .addParameter(TikXmlConfig::class.java, tikConfigParam)
            .addParameter(valueType, valueParam)
            .addException(IOException::class.java)

    private fun assignTextContentMethodBuilder(valueType: ClassName) = MethodSpec.methodBuilder("assignTextContent")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PROTECTED)
            .addParameter(TikXmlConfig::class.java, tikConfigParam)
            .addParameter(String::class.java, textContentParam)
            .addParameter(valueType, valueParam)

    /**
     * Used to specify whether we are going to assign an xml attribute or an xml element text content
     */
    private enum class AssignmentType {
        ATTRIBUTE,
        ELEMENT;

        fun xmlReaderMethodPrefix() = when (this) {
            ATTRIBUTE -> "nextAttributeValue"
            ELEMENT -> "nextTextContent"
        }
    }
}
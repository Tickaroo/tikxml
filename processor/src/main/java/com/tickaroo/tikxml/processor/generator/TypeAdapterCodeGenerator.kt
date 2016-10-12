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
import com.tickaroo.tikxml.processor.field.AnnotatedClass
import com.tickaroo.tikxml.typeadapter.DelegatingTypeAdapter
import com.tickaroo.tikxml.typeadapter.TypeAdapter
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements

/**
 * This class takes an [com.tickaroo.tikxml.processor.field.AnnotatedClass] as input
 * and generates a [com.tickaroo.tikxml.TypeAdapter] (java code) for it
 * @author Hannes Dorfmann
 * @since 1.0
 */
class TypeAdapterCodeGenerator(private val filer: Filer, private val elementUtils: Elements, private val typeConvertersForPrimitives: Set<String>) {


    /**
     * Generates an [com.tickaroo.tikxml.TypeAdapter] for the given class
     */
    fun generateCode(annotatedClass: AnnotatedClass) {

        val annotatedClassType = ClassName.get(annotatedClass.element)
        val genericParamTypeAdapter = ParameterizedTypeName.get(ClassName.get(DelegatingTypeAdapter::class.java), annotatedClassType)

        val customTypeConverterManager = CustomTypeConverterManager()
        val codeGenUtils = CodeGeneratorHelper(customTypeConverterManager, typeConvertersForPrimitives, annotatedClassType)

        val assignTextContentBuilder = codeGenUtils.assignTextContentMethodBuilder()

        if (annotatedClass.hasTextContent()) {
            // val textContentElement = annotatedClass.textContentField!!.element
            // TODO FieldAccessPolicy
            val field = annotatedClass.textContentField!!
            assignTextContentBuilder.addCode(field.accessResolver.resolveAssignment(CodeGeneratorHelper.textContentParam))
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
                .addCode(codeGenUtils.generateAttributeBinders(annotatedClass))

        for ((xmlName, xmlElement) in annotatedClass.childElements) {
            constructorBuilder.addStatement("${CodeGeneratorHelper.childElementBindersParam}.put(\$S, \$L)", xmlName, xmlElement.generateReadXmlCode(codeGenUtils))
        }


        val adapterClassBuilder = TypeSpec.classBuilder(annotatedClass.simpleClassName + TypeAdapter.GENERATED_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .superclass(genericParamTypeAdapter)
                .addMethod(constructorBuilder.build())
                .addMethod(newInstance)

        if (annotatedClass.hasTextContent()) {
            adapterClassBuilder.addMethod(assignTextContentBuilder.build())
        }

        // Add fields from TypeConverter
        for ((qualifiedConverterClass, fieldName) in customTypeConverterManager.converterMap) {
            val converterClassName = ClassName.get(elementUtils.getTypeElement(qualifiedConverterClass))
            adapterClassBuilder.addField(FieldSpec.builder(converterClassName, fieldName, Modifier.PRIVATE).initializer("new \$T()", converterClassName).build())
        }


        val packageElement = elementUtils.getPackageOf(annotatedClass.element)
        val packageName = if (packageElement.isUnnamed) "" else packageElement.toString()

        val javaFile = JavaFile.builder(packageName, adapterClassBuilder.build()).build()
        javaFile.writeTo(filer)
    }

}
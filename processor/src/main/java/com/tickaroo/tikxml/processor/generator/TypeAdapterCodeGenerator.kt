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

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.tickaroo.tikxml.typeadapter.TypeAdapter
import com.tickaroo.tikxml.processor.field.AnnotatedClass
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements

/**
 * This class takes an [com.tickaroo.tikxml.processor.field.AnnotatedClass] as input
 * and generates a [com.tickaroo.tikxml.TypeAdapter] (java code) for it
 * @author Hannes Dorfmann
 * @since 1.0
 */
class TypeAdapterCodeGenerator(private val filer: Filer, private val elementUtils: Elements) {


    /**
     * Generates an [com.tickaroo.tikxml.TypeAdapter] for the given class
     */
    fun generateCode(annotatedClass: AnnotatedClass) {


        val annotatedClassType = ClassName.get(annotatedClass.element)
        val genericParamTypeAdapter = ParameterizedTypeName.get(ClassName.get(TypeAdapter::class.java), annotatedClassType)

        /*
        val adapterClass = TypeSpec.classBuilder(annotatedClass.simpleClassName + TypeAdapter.GENERATED_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(genericParamTypeAdapter)
                .build()
*/


        val packageElement = elementUtils.getPackageOf(annotatedClass.element)
        val packageName = if (packageElement.isUnnamed) "" else packageElement.toString()

        val adapterClass = TypeSpec.classBuilder(annotatedClass.simpleClassName + TypeAdapter.GENERATED_CLASS_SUFFIX).addModifiers(Modifier.PUBLIC).build()
        val javaFile = JavaFile.builder(packageName, adapterClass).build()
        javaFile.writeTo(filer)
    }

}
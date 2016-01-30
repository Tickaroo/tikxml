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

package com.tickaroo.tikxml.processor.field

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.tickaroo.tikxml.processor.generator.CodeGenUtils
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement

/**
 * This class represents a field annotated with [com.tickaroo.tikxml.annotation.PropertyElement]
 * @author Hannes Dorfmann
 */
class PropertyField(element: VariableElement, name: String, required: Boolean? = null, val converterQualifiedName: String? = null) : NamedField(element, name, required), XmlChildElement {
    override val attributes = LinkedHashMap<String, AttributeField>()
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override fun isXmlElementAccessableFromOutsideTypeAdapter() = true

    override fun generateReadXmlCode(codeGenUtils: CodeGenUtils): TypeSpec {

        if (!hasAttributes()) {
            val fromXmlMethod = codeGenUtils.fromXmlMethodBuilder()
                    .addCode(codeGenUtils.ignoreAttributes())
                    .addCode(codeGenUtils.assignViaTypeConverterOrPrimitive(element, CodeGenUtils.AssignmentType.ELEMENT, accessPolicy, converterQualifiedName))
                    .build()


            return TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(codeGenUtils.childElementBinderType)
                    .addMethod(fromXmlMethod)
                    .build()
        }


        val fromXmlMethod = codeGenUtils.fromXmlMethodBuilder()
                .addCode(codeGenUtils.assignViaTypeConverterOrPrimitive(element, CodeGenUtils.AssignmentType.ELEMENT, accessPolicy, converterQualifiedName))
                .build()

        // Multiple attributes
        val attributeMapType = ParameterizedTypeName.get(ClassName.get(Map::class.java), ClassName.get(String::class.java), codeGenUtils.attributeBinderType)
        val attributeHashMapType = ParameterizedTypeName.get(ClassName.get(Map::class.java), ClassName.get(String::class.java), codeGenUtils.attributeBinderType)
        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGenUtils.nestedChildElementBinderType)
                .addField(FieldSpec.builder(attributeMapType, CodeGenUtils.attributeBindersParam, Modifier.PRIVATE)
                        .initializer("new \$T()", attributeHashMapType)
                        .build())
                .addInitializerBlock(codeGenUtils.generateAttributeBinders(this))
                .addMethod(fromXmlMethod)
                .build()


    }
}
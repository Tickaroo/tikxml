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

import com.squareup.javapoet.*
import com.tickaroo.tikxml.processor.generator.CodeGeneratorHelper
import com.tickaroo.tikxml.processor.utils.ifValueNotNullCheck
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement

/**
 * This class represents a field annotated with [com.tickaroo.tikxml.annotation.PropertyElement]
 * @author Hannes Dorfmann
 */
class PropertyField(element: VariableElement, name: String, val writeAsCData: Boolean = false, val converterQualifiedName: String? = null) : NamedField(element, name), XmlChildElement {
    override val attributes = LinkedHashMap<String, AttributeField>()
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override fun isXmlElementAccessableFromOutsideTypeAdapter() = true

    override fun generateReadXmlCode(codeGeneratorHelper: CodeGeneratorHelper): TypeSpec {

        if (!hasAttributes()) {
            val fromXmlMethod = codeGeneratorHelper.fromXmlMethodBuilder()
                    .addCode(codeGeneratorHelper.ignoreAttributes())
                    .addCode(codeGeneratorHelper.assignViaTypeConverterOrPrimitive(element, CodeGeneratorHelper.AssignmentType.ELEMENT, accessResolver, converterQualifiedName))
                    .build()


            return TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(codeGeneratorHelper.childElementBinderType)
                    .addMethod(fromXmlMethod)
                    .build()
        }


        val fromXmlMethod = codeGeneratorHelper.fromXmlMethodBuilder()
                .addCode(codeGeneratorHelper.assignViaTypeConverterOrPrimitive(element, CodeGeneratorHelper.AssignmentType.ELEMENT, accessResolver, converterQualifiedName))
                .build()

        // Multiple attributes
        val attributeMapType = ParameterizedTypeName.get(ClassName.get(Map::class.java), ClassName.get(String::class.java), codeGeneratorHelper.attributeBinderType)
        val attributeHashMapType = ParameterizedTypeName.get(ClassName.get(Map::class.java), ClassName.get(String::class.java), codeGeneratorHelper.attributeBinderType)
        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGeneratorHelper.nestedChildElementBinderType)
                .addField(FieldSpec.builder(attributeMapType, CodeGeneratorHelper.attributeBindersParam, Modifier.PRIVATE)
                        .initializer("new \$T()", attributeHashMapType)
                        .build())
                .addInitializerBlock(codeGeneratorHelper.generateAttributeBinders(this))
                .addMethod(fromXmlMethod)
                .build()


    }

    override fun generateWriteXmlCode(codeGeneratorHelper: CodeGeneratorHelper) =
            CodeBlock.builder()
                    .ifValueNotNullCheck(this) {
                        add(codeGeneratorHelper.writeBeginElementAndAttributes(this@PropertyField))
                        add(codeGeneratorHelper.writeTextContentViaTypeConverterOrPrimitive(element, accessResolver, converterQualifiedName, writeAsCData))
                        addStatement("${CodeGeneratorHelper.writerParam}.endElement()")
                    }
                    .build()

}
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
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.tickaroo.tikxml.processor.generator.CodeGenUtils
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import java.util.*
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 * Represents a Field with [com.tickaroo.tikxml.annotation.Element] annotation
 * @author Hannes Dorfmann
 */
open class ElementField(element: VariableElement, name: String, required: Boolean? = null) : NamedField(element, name, required), XmlChildElement {

    override val attributes = LinkedHashMap<String, AttributeField>()
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override fun isXmlElementAccessableFromOutsideTypeAdapter() = false

    override fun generateReadXmlCode(codeGenUtils: CodeGenUtils): TypeSpec {

        val fromXmlMethod = codeGenUtils.fromXmlMethodBuilder()
                .addCode(accessPolicy.resolveAssignment("${CodeGenUtils.tikConfigParam}.getTypeAdapter(\$T.class).fromXml(${CodeGenUtils.readerParam}, ${CodeGenUtils.tikConfigParam})", ClassName.get(element.asType())))
                .build()

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGenUtils.childElementBinderType)
                .addMethod(fromXmlMethod)
                .build()

    }
}

class ListElementField(element: VariableElement, name: String, required: Boolean? = null, private val genericListType: TypeMirror) : ElementField(element, name, required) {


    override fun generateReadXmlCode(codeGenUtils: CodeGenUtils): TypeSpec {


        val valueTypeAsArrayList = ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), ClassName.get(genericListType))


        val valueFromAdapter = "${CodeGenUtils.tikConfigParam}.getTypeAdapter(\$T.class).fromXml(${CodeGenUtils.readerParam}, ${CodeGenUtils.tikConfigParam})"

        val fromXmlMethod = codeGenUtils.fromXmlMethodBuilder()
                .addCode(CodeBlock.builder()
                        .beginControlFlow("if (${accessPolicy.resolveGetter()} == null)")
                        .add(accessPolicy.resolveAssignment("new \$T()", valueTypeAsArrayList))
                        .endControlFlow()
                        .build())
                .addStatement("${accessPolicy.resolveGetter()}.add((\$T) $valueFromAdapter )", ClassName.get(genericListType), ClassName.get(genericListType))
                .build()

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGenUtils.childElementBinderType)
                .addMethod(fromXmlMethod)
                .build()

    }

}
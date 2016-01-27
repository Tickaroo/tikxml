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
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.field.access.FieldAccessPolicy
import com.tickaroo.tikxml.processor.generator.CodeGenUtils
import java.util.*
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 * Represents a Field with [com.tickaroo.tikxml.annotation.Element] annotation
 * @author Hannes Dorfmann
 */
open class PolymorphicElementField(element: VariableElement, name: String, required: Boolean?, val typeElementNameMatcher: List<PolymorphicTypeElementNameMatcher>) : ElementField(element, name, required) {
    override fun generateReadXmlCode(codeGenUtils: CodeGenUtils): TypeSpec {
        return codeGenUtils.generateNestedChildElementBinder(this)
    }
}

class PolymorphicListElementField(element: VariableElement, name: String, required: Boolean?, typeElementNameMatcher: List<PolymorphicTypeElementNameMatcher>, val genericListTypeMirror: TypeMirror) : PolymorphicElementField(element, name, required, typeElementNameMatcher) {

    override fun generateReadXmlCode(codeGenUtils: CodeGenUtils): TypeSpec {
        throw ProcessingException(element, "Oops, en error has occurred while generating reading xml code for $this. Please fill an issue at https://github.com/Tickaroo/tikxml/issues")
    }
}

/**
 * This kind of element will be used to replace a [PolymorphicElementField]
 */
open class PolymorphicSubstitutionField(element: VariableElement, override val typeMirror: TypeMirror, override var accessPolicy: FieldAccessPolicy, name: String, required: Boolean? = null) : ElementField(element, name, required) {

    override fun isXmlElementAccessableFromOutsideTypeAdapter(): Boolean = false

    override fun generateReadXmlCode(codeGenUtils: CodeGenUtils): TypeSpec {

        val fromXmlMethod = codeGenUtils.fromXmlMethodBuilder()
                .addCode(accessPolicy.resolveAssignment("${CodeGenUtils.tikConfigParam}.getTypeAdapter(\$T.class).fromXml(${CodeGenUtils.readerParam}, ${CodeGenUtils.tikConfigParam})", ClassName.get(typeMirror)))
                .build()

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGenUtils.childElementBinderType)
                .addMethod(fromXmlMethod)
                .build()
    }

}

/**
 * This kind of element will be used to replace a [PolymorphicElementField] but for List elements
 */
class PolymorphicSubstitutionListField(element: VariableElement, typeMirror: TypeMirror, accessPolicy: FieldAccessPolicy, name: String, private val genericListTypeMirror: TypeMirror, required: Boolean? = null) : PolymorphicSubstitutionField(element, typeMirror, accessPolicy, name, required) {


    override fun generateReadXmlCode(codeGenUtils: CodeGenUtils): TypeSpec {

        val valueTypeAsArrayList = ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), ClassName.get(genericListTypeMirror))

        val valueFromAdapter = "${CodeGenUtils.tikConfigParam}.getTypeAdapter(\$T.class).fromXml(${CodeGenUtils.readerParam}, ${CodeGenUtils.tikConfigParam})"

        val fromXmlMethod = codeGenUtils.fromXmlMethodBuilder()
                .addCode(CodeBlock.builder()
                        .beginControlFlow("if (${accessPolicy.resolveGetter()} == null)")
                        .add(accessPolicy.resolveAssignment("new \$T()", valueTypeAsArrayList))
                        .endControlFlow()
                        .build())
                .addStatement("\$T v = $valueFromAdapter", ClassName.get(typeMirror), ClassName.get(typeMirror))
                .addStatement("${accessPolicy.resolveGetter()}.add(v)")
                .build()

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGenUtils.childElementBinderType)
                .addMethod(fromXmlMethod)
                .build()
    }

}

data class PolymorphicTypeElementNameMatcher(val xmlElementName: String, val type: TypeMirror)
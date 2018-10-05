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

package com.tickaroo.tikxml.processor.xml

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeSpec
import com.tickaroo.tikxml.processor.field.AttributeField
import com.tickaroo.tikxml.processor.generator.CodeGeneratorHelper
import com.tickaroo.tikxml.processor.utils.endXmlElement
import com.tickaroo.tikxml.processor.utils.getSurroundingClassQualifiedName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import kotlin.collections.LinkedHashMap

/**
 * This element represents a "placeholder" xml element. That means that we might have written a [com.tickaroo.tikxml.annotation.Path]
 * annotation that is pointing to a virtual element, then this Placeholder element will be instantiated and eventually be merged
 * with a real element afterwards.
 * @author Hannes Dorfmann
 */
class PlaceholderXmlElement(override val name: String, override val element: Element) : XmlChildElement {


    override val attributes = LinkedHashMap<String, AttributeField>()
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override fun isXmlElementAccessableFromOutsideTypeAdapter() = true

    override fun toString(): String = when (element) {
        is VariableElement -> "field '${element.simpleName}' in class ${element.getSurroundingClassQualifiedName()}"
        is TypeElement -> element.qualifiedName.toString()
        else -> throw IllegalArgumentException("Oops, unexpected element type $element. This should never happen. Please fill an issue here: https://github.com/Tickaroo/tikxml/issues")
    }

    override fun generateReadXmlCode(codeGeneratorHelper: CodeGeneratorHelper): TypeSpec {
        return codeGeneratorHelper.generateNestedChildElementBinder(this)
    }

    override fun generateWriteXmlCode(codeGeneratorHelper: CodeGeneratorHelper) =
            CodeBlock.builder()
                    .add(codeGeneratorHelper.writeBeginElementAndAttributes(this))
                    .add(codeGeneratorHelper.writeChildrenByResolvingPolymorphismElementsOrFieldsOrDelegateToChildCodeGenerator(this))
                    .endXmlElement()
                    .build()
}
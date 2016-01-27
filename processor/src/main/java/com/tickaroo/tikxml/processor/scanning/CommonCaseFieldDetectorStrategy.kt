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

package com.tickaroo.tikxml.processor.scanning

import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.field.AttributeField
import com.tickaroo.tikxml.processor.field.ElementField
import com.tickaroo.tikxml.processor.field.ListElementField
import com.tickaroo.tikxml.processor.field.NamedField
import com.tickaroo.tikxml.processor.utils.getSurroundingClassQualifiedName
import com.tickaroo.tikxml.processor.utils.isAbstract
import com.tickaroo.tikxml.processor.utils.isInterface
import com.tickaroo.tikxml.processor.utils.isPrimitive
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * The common case strategy is described in [com.tickaroo.tikxml.annotation.ScanMode]
 * @author Hannes Dorfmann
 */
class CommonCaseFieldDetectorStrategy(elementUtils: Elements, typeUtils: Types, requiredDetector: RequiredDetector) : AnnotationOnlyFieldDetectorStrategy(elementUtils, typeUtils, requiredDetector) {

    override fun isXmlField(element: VariableElement): NamedField? {

        if (ignoreField(element)) {
            return null
        }

        if (isTextContentAnnotated(element)) {
            return null
        }

        val field = super.isXmlField(element)
        if (field != null) {
            return field
        }

        // fields are treated as attributes
        if (element.asType().isPrimitive()) {
            return AttributeField(
                    element,
                    element.simpleName.toString(),
                    requiredDetector.isRequired(element),
                    null
            )
        }

        // Objects are treated as Element
        if (element.asType().kind == TypeKind.DECLARED) {

            if (isList(element)) {

                val genericListType = getGenericTypeFromList(element)
                val genericListTypeElement = typeUtils.asElement(genericListType) as TypeElement

                if (genericListTypeElement.isInterface()) {
                    throw ProcessingException(element, "The generic list type of '$element' in class ${element.getSurroundingClassQualifiedName()} is an interface. Hence polymorphism must be resolved manually by using @${ElementNameMatcher::class.simpleName}.")
                }

                if (genericListTypeElement.isAbstract()) {
                    throw ProcessingException(element, "The generic list type of '$element' in class ${element.getSurroundingClassQualifiedName()} is a abstract class. Hence polymorphism must be resolved manually by using @${ElementNameMatcher::class.simpleName}.")
                }

                val elementName = getXmlElementNameOrThrowException(element, genericListTypeElement)


                return ListElementField(
                        element,
                        elementName,
                        requiredDetector.isRequired(element),
                        getGenericTypeFromList(element)
                )
            }

            val elementType = (element.asType() as DeclaredType).asElement() as TypeElement

            if (elementType.isInterface()) {
                throw ProcessingException(element, "The field '$element' in class ${element.getSurroundingClassQualifiedName()} is of a type that is an interface. Hence polymorphism must be resolved manually by using @${ElementNameMatcher::class.simpleName}.")
            }

            if (elementType.isAbstract()) {
                throw ProcessingException(element, "The field '$element' in ${element.getSurroundingClassQualifiedName()} is of a type that is an abstract class. Hence polymorphism must be resolved manually by using @${ElementNameMatcher::class.simpleName}.")
            }

            return ElementField(
                    element,
                    getXmlElementNameOrThrowException(element, elementType),
                    requiredDetector.isRequired(element)
            )
        }

        return null
    }

}
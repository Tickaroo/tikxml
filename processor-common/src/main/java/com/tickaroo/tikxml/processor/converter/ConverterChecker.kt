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

package com.tickaroo.tikxml.processor.converter

import com.tickaroo.tikxml.TypeConverter
import com.tickaroo.tikxml.processor.ProcessingException
import java.lang.reflect.Modifier
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import kotlin.reflect.KClass

/**
 * Class that checks if the given converter class matches the required criterias
 * @author Hannes Dorfmann
 */
abstract class ConverterChecker<in T : Annotation> {

    // TODO add check if TypeConverter is applyable for the given annotated field type

    /**
     * Checks if the given class is valid
     * @return null if no TypeConverter should be used, otherwise the full qualified name of the type converter
     */
    fun getQualifiedConverterName(element: Element, annotation: T): String? {

        try {
            // Already compiled class
            val converterClass = getConverterFromAnnotation(annotation).java

            // No type converter
            if (converterClass == TypeConverter.NoneTypeConverter::class.java) {
                return null
            }


            // Class must be public
            if (!Modifier.isPublic(converterClass.modifiers)) {
                throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} must be a public class!")
            }

            if (Modifier.isAbstract(converterClass.modifiers)) {
                throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} cannot be a abstract")
            }

            if (Modifier.isInterface(converterClass.modifiers)) {
                throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} cannot be an interface. Only classes are allowed!")
            }


            // Must have default constructor
            val constructors = converterClass.constructors
            for (c in constructors) {
                val isPublicConstructor = Modifier.isPublic(c.modifiers);
                val paramTypes = c.parameterTypes;

                if (paramTypes.isEmpty() && isPublicConstructor) {
                    return converterClass.canonicalName
                }
            }

            // No public constructor found
            throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} must provide an empty (parameter-less) public constructor")


        } catch(mte: MirroredTypeException) {

            // Not compiled class
            val typeMirror = mte.typeMirror

            if (typeMirror.toString() == TypeConverter.NoneTypeConverter::class.qualifiedName) {
                return null
            }

            if (typeMirror.kind != TypeKind.DECLARED) {
                throw ProcessingException(element, "TypeConverter must be a class")
            }

            val typeConverterType = typeMirror as DeclaredType
            val typeConverterElement = typeConverterType.asElement()

            if (typeConverterElement.kind != ElementKind.CLASS) {
                throw ProcessingException(element, "TypeConverter ${typeConverterElement} must be a public class!")
            }

            if (!typeConverterElement.modifiers.contains(javax.lang.model.element.Modifier.PUBLIC)) {
                throw ProcessingException(element, "TypeConverter ${typeConverterElement} class is not public!")
            }

            // Check empty constructor
            for (e in (typeConverterElement as TypeElement).enclosedElements) {
                if (e.kind == ElementKind.CONSTRUCTOR) {
                    val constructor = e as ExecutableElement
                    if (constructor.modifiers.contains(javax.lang.model.element.Modifier.PUBLIC)
                            && constructor.parameters.isEmpty()) {
                        return typeMirror.toString()
                    }
                }
            }

            throw ProcessingException(element, "TypeConverter class ${typeMirror} must provide an empty (parameter-less) public constructor")
        }

    }

    /**
     * Extracts the type Converter class from a given annotation
     */
    abstract protected fun getConverterFromAnnotation(annotation: T): KClass<out TypeConverter<Any>>

}
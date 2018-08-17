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

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.field.*
import com.tickaroo.tikxml.processor.field.access.FieldAccessResolver
import com.tickaroo.tikxml.processor.utils.*
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Scans an [com.tickaroo.tikxml.annotation.Xml] annotated class and fulfills the  [AnnotatedClass] by using [AnnotationDetector]
 * @author Hannes Dorfmann
 * @since 1.0
 */
class AnnotationScanner(protected val elementUtils: Elements, protected val typeUtils: Types, private val annotationDetector: AnnotationDetector) {

    val booleanFieldRegex = Regex("is[A-Z].*")

    /**
     * Scans the child element of the passed [AnnotatedClass] to find [com.tickaroo.tikxml.processor.field.NamedField]
     */
    @Throws(ProcessingException::class)
    fun scan(annotatedClass: AnnotatedClass) {

        val currentElement: TypeElement = annotatedClass.element

        // Scan the currently annotated class
        val hasAnnotatedConstructor = doScan(annotatedClass, currentElement)

        if (!hasAnnotatedConstructor && annotatedClass.inheritance) {
            // Scan super classes with @Xml annotation
            currentElement.getSuperClasses(typeUtils)
                    .filter { it.getAnnotation(Xml::class.java) != null }
                    .forEach { doScan(annotatedClass, it) }
        }
    }

    /**
     * Scan a class for annotated fields and constructors.
     * @return true, if the scanned TypeElement has an annotated constructor, otherwise false
     */
    private fun doScan(annotatedClass: AnnotatedClass, currentElement: TypeElement): Boolean {

        val fieldWithMethodAccessRequired = ArrayList<Field>()
        val methodsMap = HashMap<String, ExecutableElement>()
        var constructorFound = false
        var annotatedConstructor: ExecutableElement? = null
        val annotatedConstructorFields = ArrayList<Field>()
        var annotatedFields = 0

        // Function to
        val checkAccessPolicyOrDeferGetterSetterCheck = fun(element: VariableElement, field: Field): Boolean {
            if (element.hasMinimumPackageVisibilityModifiers()) {
                field.accessResolver = FieldAccessResolver.MinPackageVisibilityFieldAccessResolver(field.element)
                return true
            } else {
                fieldWithMethodAccessRequired.add(field) // processed afterwards
                return false
            }
        }

        currentElement.enclosedElements.forEach {

            if (it.isEmptyConstructorWithMinimumPackageVisibility()) {
                constructorFound = true
            } else if (it.isConstructor() && !it.isEmptyConstructor()) {
                // check for Annotated Constructors

                val constructor = it as ExecutableElement
                var foundXmlAnnotation = 0
                constructor.parameters.forEach {
                    val mappableField = fillAnnotatedClass(annotatedClass, it, checkAccessPolicyOrDeferGetterSetterCheck)
                    if (mappableField != null) {
                        foundXmlAnnotation++
                        annotatedConstructorFields.add(mappableField)
                    }
                }
                // check if constructor contains a mix of mappable (xml annotated) parameters and not mappable which is not allowed
                // Either all parameters are mappable or none of them is
                if (foundXmlAnnotation == constructor.parameters.size) {

                    if (!constructor.hasMinimumPackageVisibilityModifiers()) {
                        throw ProcessingException(constructor, "The constructor $constructor in class ${annotatedClass.qualifiedClassName} is annotated with TikXml annotations and therefore must have at least package visibility to be able to be invoked from TikXml")
                    }

                    if (annotatedClass.inheritance) {
                        // For annotated classes we have to scan inheritance tree for methods
                        annotatedClass.element.getSuperClasses(typeUtils).flatMap {
                            it.enclosedElements.filter { method -> method.isGetterMethodWithMinimumPackageVisibility() }.map { it as ExecutableElement }
                        }.forEach { methodsMap.put(it.simpleName.toString(), it) }
                    }

                    // Only one annotated constructor is allowed: Already an annotated constructor found
                    if (annotatedConstructor != null) {
                        throw ProcessingException(annotatedConstructor, "Only one constructor with TikXml annotated parameters is allowed but found multiple constructors with annotated parameters in class ${annotatedClass.qualifiedClassName}:    1) $annotatedConstructor    2) $constructor")
                    }

                    annotatedConstructor = constructor
                    constructorFound = true
                } else if (foundXmlAnnotation > 0) {
                    throw ProcessingException(constructor, "The constructor $constructor in class ${annotatedClass.qualifiedClassName} contains a mix of TikXml annotated parameters and not annotated parameters." +
                            "That is not allowed! Either annotate all parameters or none of them")
                } // Otherwise it was an empty constructor.

            } else if (it.isGetterMethodWithMinimumPackageVisibility() || it.isSetterMethodWithMinimumPackageVisibility()) {
                val method = it as ExecutableElement
                methodsMap.put(method.simpleName.toString(), method)
            } else if (it.isField()) {
                if (currentElement.getAnnotation(Xml::class.java) == null) {
                    throw ProcessingException(currentElement, "The class ${annotatedClass.qualifiedClassName} should be annotated with @${Xml::class.simpleName}. This is an internal error. Please file an issue on github: https://github.com/Tickaroo/tikxml/issues") // Should be impossible
                }

                val field = fillAnnotatedClass(annotatedClass, it as VariableElement, checkAccessPolicyOrDeferGetterSetterCheck)
                if (field != null) {
                    annotatedFields++
                }
            }
        }


        if (!constructorFound) {
            throw ProcessingException(annotatedClass.element, "${annotatedClass.qualifiedClassName} " +
                    "must provide an empty (parameterless) constructor with minimum default (package) visibility")
        }

        if (annotatedConstructor != null) {

            if (annotatedFields > 0) {
                throw ProcessingException(annotatedConstructor, "${annotatedClass.qualifiedClassName} has TikXml annotated fields AND an annotated constructor $annotatedConstructor . That is not allowed! Either annotate fields or a constructor (but not a mix of both)")
            }

            // Check if all constructor annotated fields have a getter method
            annotatedConstructorFields.forEach {
                val getter = checkGetter(it, methodsMap, true)
                it.accessResolver = FieldAccessResolver.ConstructorAndGetterFieldAccessResolver(it.element, getter)
                if (it is PolymorphicElementField) {
                    it.substitutions.forEach { sub -> sub.accessResolver = it.accessResolver }
                }
            }

            annotatedClass.annotatedConstructor = annotatedConstructor

        } else {
            // Not annotated constructor
            // Search for getters and setters
            fieldWithMethodAccessRequired.forEach {
                val getter = checkGetter(it, methodsMap, false)
                val setter = checkSetter(it, methodsMap)

                // Set access policy
                it.accessResolver = FieldAccessResolver.GetterSetterFieldAccessResolver(getter, setter)

                if (it is NamedField) {
                    addFieldToAnnotatedClass(annotatedClass, it)
                }
            }
        }
        return annotatedConstructor != null
    }

    /**
     * Searches for a getter method for the given field
     */
    fun checkGetter(field: Field, methodsMap: Map<String, ExecutableElement>, constructorAnnotatedField: Boolean): ExecutableElement {
        val element = field.element
        val elementName: String = element.simpleName.toString()
        val nameWithoutHungarian = getFieldNameWithoutHungarianNotation(element)

        var getter = findGetterForField(element, nameWithoutHungarian, "get", methodsMap)
        if (getter == null) {
            getter = findGetterForHungarianField(element, elementName, "get", methodsMap)
            if (getter == null) {
                getter = findGetterForHungarianFieldUpperCase(element, elementName, "get", methodsMap)
            }
        }

        // Test with "is" prefix
        if (getter == null && element.asType().isBoolean()) {
            getter = findGetterForField(element, nameWithoutHungarian, "is", methodsMap)
            if (getter == null) {
                getter = findGetterForHungarianField(element, elementName, "is", methodsMap)
                if (getter == null) {
                    getter = findGetterForHungarianFieldUpperCase(element, elementName, "is", methodsMap)
                }
            }
        }

        if (getter == null) {
            if (constructorAnnotatedField) {
                val constructor = element.enclosingElement
                throw ProcessingException(element, "The constructor parameter '${element}' "
                        + "in constructor $constructor in class ${(constructor.enclosingElement as TypeElement).qualifiedName.toString()} "
                        + " is annotated with a TikXml annotation. Therefore a getter method"
                        + " with minimum package visibility"
                        + " with the name ${bestMethodName(elementName, "get")}() or ${bestMethodName(elementName, "is")}() "
                        + "in case of a boolean must be provided. Unfortunately, there is no such getter method. Please provide one!")
            } else {
                throw ProcessingException(element, "The field '${element.simpleName.toString()}' "
                        + "in class ${(element.enclosingElement as TypeElement).qualifiedName.toString()} "
                        + "has private or protected visibility. Hence a corresponding getter method must be provided"
                        + " with minimum package visibility (or public visibility if this is a super class in a different package)"
                        + " with the name ${bestMethodName(elementName, "get")}() or ${bestMethodName(elementName, "is")}() "
                        + "in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
            }
        }

        if (!getter.isParameterlessMethod()) {
            throw ProcessingException(element, "The getter method '${getter.toString()}' for field '${element.simpleName.toString()}'"
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must be parameterless (zero parameters).")
        }

        if (getter.isProtected() || getter.isPrivate() || (getter.isDefaultVisibility() && !getter.isSamePackageAs(element, elementUtils))) {
            throw ProcessingException(element, "The getter method '${getter.toString()}' for field '${element.simpleName.toString()}' "
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must have minimum package visibility (or public visibility if this is a super class in a different package)")

        }

        return getter
    }

    /**
     * Searches for a setter method for the given field
     */
    fun checkSetter(field: Field, methodsMap: Map<String, ExecutableElement>): ExecutableElement {
        val element = field.element
        val elementName: String = element.simpleName.toString()
        val nameWithoutHungarian = getFieldNameWithoutHungarianNotation(element)

        // Setter method
        var setter = findMethodForField(nameWithoutHungarian, "set", methodsMap)
        if (setter == null) {
            setter = findMethodForHungarianField(elementName, "set", methodsMap)
            if (setter == null) {
                setter = findMethodForHungarianFieldUpperCase(elementName, "set", methodsMap)
            }
        }

        if (setter == null) {
            throw ProcessingException(element, "The field '${element.simpleName.toString()}' "
                    + "in class ${(element.enclosingElement as TypeElement).qualifiedName.toString()} "
                    + "has private or protected visibility. Hence a corresponding setter method must be provided "
                    + " with the name ${bestMethodName(elementName, "set")}(${element.asType()}) and "
                    + "minimum package visibility (or public visibility if this is a super class in a different package)"
                    + "Unfortunately, there is no such setter method. Please provide one!")
        }

        if (!setter.isMethodWithOneParameterOfType(element.asType(), typeUtils)) {
            throw ProcessingException(element, "The setter method '${setter.toString()}' for field '${element.simpleName.toString()}' "
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must have exactly one parameter of type '${element.asType()}'")
        }

        if (setter.isProtected() || setter.isPrivate() || (setter.isDefaultVisibility() && !setter.isSamePackageAs(element, elementUtils))) {
            throw ProcessingException(element, "The setter method '${setter.toString()}' for field '${element.simpleName.toString()}'"
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must have minimum package visibility (or public visibility if this is a super class in a different package)")

        }
        return setter
    }

    /**
     * Scans for xml mappable fields. If mappable field has been found, it will be added to internal class
     * @return true if a mappable Element has been found, otherwise false
     */
    private inline fun fillAnnotatedClass(annotatedClass: AnnotatedClass, it: VariableElement, checkAccessPolicyOrDeferGetterSetterCheck: (VariableElement, Field) -> Boolean): Field? {
        // TODO support for @TextContent + @Path
        val textContentField = annotationDetector.isXmlTextContent(it)

        // TextContent Field
        if (annotatedClass.textContentField == null && textContentField != null) {

            // TODO check for multiple text content definition in one class / constructor

            // Only take the first @TextContent field if there are multiple in the inheritance tree
            annotatedClass.textContentField = textContentField
            checkAccessPolicyOrDeferGetterSetterCheck(it, textContentField)
            return textContentField
        }

        val field: NamedField? = annotationDetector.isXmlField(it)
        if (field != null) {

            if (textContentField != null) {
                // @TextContent annotation and @Attribute, @PropertyElement or @Element at the same time
                // TODO I think this is dead code (will never be reached)
                throw ProcessingException(it, "Field '$it' is marked as TextContent and another xml element like @${Attribute::class.simpleName}, @${PropertyElement::class.simpleName} or @${Element::class.simpleName} at the same time which is not allowed. A field can only be exactly one of those types.")
            }

            // needs setter and getter?
            if (checkAccessPolicyOrDeferGetterSetterCheck(it, field)) {
                addFieldToAnnotatedClass(annotatedClass, field)
            }
        }

        return field
    }

    private fun addFieldToAnnotatedClass(annotatedClass: AnnotatedClass, field: NamedField): Unit =
            when (field) {

                is AttributeField -> annotatedClass.addAttribute(field, PathDetector.getSegments(field.element))

                is PolymorphicListElementField -> {
                    val path = PathDetector.getSegments(field.element)
                    for ((xmlElementName, typeMirror) in field.typeElementNameMatcher) {
                        val sub = PolymorphicSubstitutionListField(field.element, typeMirror, field.accessResolver, xmlElementName, field.genericListTypeMirror)
                        field.substitutions.add(sub)
                        annotatedClass.addChildElement(sub, path)
                    }
                }

                is PolymorphicElementField -> {
                    // Insert PolymorphicSubstitution instead of the original field.
                    for ((xmlElementName, typeMirror) in field.typeElementNameMatcher) {
                        val sub = PolymorphicSubstitutionField(field.element, typeMirror, field.accessResolver, xmlElementName, field.typeMirror)
                        field.substitutions.add(sub)
                        annotatedClass.addChildElement(sub, PathDetector.getSegments(field.element))
                    }
                }
                is XmlChildElement -> annotatedClass.addChildElement(field, PathDetector.getSegments(field.element))

                else -> throw IllegalArgumentException("Oops, unexpected element type $field. This should never happen. Please fill an issue here: https://github.com/Tickaroo/tikxml/issues")
            }

    /**
     * Finds a method for a field. Removes hungarion notation. If field name was mFoo this method checks for a method called setFoo()
     */
    private fun findMethodForField(fieldName: String, methodNamePrefix: String, setterAndGetters: Map<String, ExecutableElement>): ExecutableElement? {
        val methodName = bestMethodName(fieldName, methodNamePrefix)
        return setterAndGetters[methodName]
    }

    private fun findGetterForField(fieldElement: VariableElement, fieldName: String, methodNamePrefix: String, setterAndGetters: Map<String, ExecutableElement>): ExecutableElement? {

        val method = findMethodForField(fieldName, methodNamePrefix, setterAndGetters) ?: return null
        return if (typeUtils.isSameType(method.returnType, fieldElement.asType())) method else null
    }

    private fun bestMethodName(fieldName: String, methodNamePrefix: String): String {

        if (fieldName.length == 1) {
            // a should be getA()
            val builder = StringBuilder(methodNamePrefix)
            builder.append(fieldName.toUpperCase())
            return builder.toString()

        } /*else if (fieldName[0].isLowerCase() && fieldName[1].isUpperCase()) {
            // aString should be getaString()
            builder.append(fieldName)

        }*/
        else if (methodNamePrefix === "is" && fieldName.matches(booleanFieldRegex)) {
            // field isFoo shoule be isFoo()
            return fieldName
        } else if (methodNamePrefix === "set" && fieldName.matches(booleanFieldRegex)) {
            // field isFoo should be setFoo()
            val builder = StringBuilder(methodNamePrefix)
            builder.append(fieldName.substring(2))
            return builder.toString()
        } else {

            // foo should be getFoo()
            val builder = StringBuilder(methodNamePrefix)
            builder.append(Character.toUpperCase(fieldName[0]))
            builder.append(fieldName.substring(1))
            return builder.toString()
        }
    }

    /**
     * If field name was mFoo this method checks for a method called setmFoo()
     */
    private fun findMethodForHungarianField(fieldName: String, methodNamePrefix: String, setterAndGetters: Map<String, ExecutableElement>): ExecutableElement? {


        // Search for setter method with hungarian notion check
        if (fieldName.length > 1 && fieldName.matches(Regex("m[A-Z].*"))) {
            // m not in lower case
            val hungarianMethodName = methodNamePrefix + fieldName;
            return setterAndGetters[hungarianMethodName];
        }
        return null;
    }

    private fun findGetterForHungarianField(fieldElement: VariableElement, fieldName: String, methodNamePrefix: String, setterAndGetters: Map<String, ExecutableElement>): ExecutableElement? {
        val method = findMethodForHungarianField(fieldName, methodNamePrefix, setterAndGetters) ?: return null
        return if (typeUtils.isSameType(method.returnType, fieldElement.asType())) method else null
    }

    /**
     * If field name was mFoo this method checks for a method called setMFoo()
     */
    private fun findMethodForHungarianFieldUpperCase(fieldName: String, methodNamePrefix: String, setterAndGetters: Map<String, ExecutableElement>): ExecutableElement? {

        // Search for setter method with hungarian notion check
        if (fieldName.length > 1 && fieldName.matches(Regex("m[A-Z].*"))) {

            // M in upper case
            val hungarianMethodName = methodNamePrefix + Character.toUpperCase(fieldName[0]) + fieldName.substring(1)
            return setterAndGetters[hungarianMethodName]
        }

        return null
    }

    private fun findGetterForHungarianFieldUpperCase(fieldElement: VariableElement, fieldName: String, methodNamePrefix: String, setterAndGetters: Map<String, ExecutableElement>): ExecutableElement? {

        val method = findMethodForHungarianFieldUpperCase(fieldName, methodNamePrefix, setterAndGetters) ?: return null
        return if (typeUtils.isSameType(method.returnType, fieldElement.asType())) method else null
    }

    private fun getFieldNameWithoutHungarianNotation(element: VariableElement): String {
        val name = element.simpleName.toString()
        if (name.matches(Regex("^m[A-Z]{1}"))) {
            return name.substring(1, 2).toLowerCase();
        } else if (name.matches(Regex("m[A-Z]{1}.*"))) {
            return name.substring(1, 2).toLowerCase() + name.substring(2);
        }
        return name;
    }
}



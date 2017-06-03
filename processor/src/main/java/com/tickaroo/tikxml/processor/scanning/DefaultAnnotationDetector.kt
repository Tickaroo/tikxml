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

import com.tickaroo.tikxml.annotation.*
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.converter.AttributeConverterChecker
import com.tickaroo.tikxml.processor.converter.PropertyElementConverterChecker
import com.tickaroo.tikxml.processor.field.*
import com.tickaroo.tikxml.processor.utils.*
import java.util.*
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.*
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * A [AnnotationScanner] that scans the element by checking for TikXml annotations
 * @author Hannes Dorfmann
 */
open class DefaultAnnotationDetector(protected val elementUtils: Elements, protected val typeUtils: Types) : AnnotationDetector {

    val listTypeMirror: TypeMirror

    init {
        listTypeMirror = typeUtils.erasure(elementUtils.getTypeElement("java.util.List").asType())
    }

    protected fun isTextContentAnnotated(element: VariableElement): Boolean {
        val textContentAnnotated = element.getAnnotation(TextContent::class.java) != null
        if (textContentAnnotated && element.getAnnotation(Path::class.java) != null) {
            throw ProcessingException(element, "@${Path::class.simpleName} on @${TextContent::class.simpleName} is not allowed. Use @${PropertyElement::class.simpleName} and @${Path::class.simpleName} instead on field '$element' in class ${element.getSurroundingClassQualifiedName()}")
        } else {
            return textContentAnnotated
        }
    }

    override fun isXmlTextContent(element: VariableElement): TextContentField? =
            if (isTextContentAnnotated(element) && isXmlField(element) == null) {
                if (!element.asType().isString()) {
                    throw ProcessingException(element, "Only type String is supported for @${TextContent::class.simpleName} but field '$element' in class ${element.getSurroundingClassQualifiedName()} is not of type String")
                }

                val annotation = element.getAnnotation(TextContent::class.java)
                TextContentField(element, annotation.writeAsCData)
            } else
                null

    override fun isXmlField(element: VariableElement): NamedField? {
        var annotationFound = 0;

        // MAIN ANNOTATIONS
        val attributeAnnotation = element.getAnnotation(Attribute::class.java)
        val propertyAnnotation = element.getAnnotation(PropertyElement::class.java)
        val elementAnnotation = element.getAnnotation(Element::class.java)
        val textContent = element.getAnnotation(TextContent::class.java)

        if (attributeAnnotation != null) {
            annotationFound++;
        }

        if (propertyAnnotation != null) {
            annotationFound++
        }

        if (elementAnnotation != null) {
            annotationFound++
        }

        if (textContent != null) {
            annotationFound++
        }

        // No annotations
        if (annotationFound == 0) {
            return null
        }

        if (annotationFound > 1) {
            // More than one annotation is not allowed
            throw ProcessingException(element, "Fields can ONLY be annotated with one of the "
                    + "following annotations @${Attribute::class.simpleName}, "
                    + "@${PropertyElement::class.simpleName}, @${Element::class.simpleName} or @${TextContent::class.simpleName}  "
                    + "and not multiple of them! The field ${element.simpleName.toString()} in class "
                    + "${(element.enclosingElement as TypeElement).qualifiedName} is annotated with more than one of these annotations. You must annotate a field with exactly one of these annotations (not multiple)!")
        }

        // In the case that only text content annotation has been found
        if (textContent != null && annotationFound == 1)
            return null;


        if (attributeAnnotation != null) {

            val converterChecker = AttributeConverterChecker()
            return AttributeField(element,
                    nameFromAnnotationOrField(attributeAnnotation.name, element),
                    converterChecker.getQualifiedConverterName(element, attributeAnnotation))
        }

        if (propertyAnnotation != null) {
            val converterChecker = PropertyElementConverterChecker()
            return PropertyField(element,
                    nameFromAnnotationOrField(propertyAnnotation.name, element),
                    propertyAnnotation.writeAsCData,
                    converterChecker.getQualifiedConverterName(element, propertyAnnotation))
        }


        if (elementAnnotation != null) {
            val nameMatchers = elementAnnotation.typesByElement

            // Check for primitives
            if (element.asType().kind != TypeKind.DECLARED) {
                throw ProcessingException(element, "The type of field '${element.simpleName}' in class ${element.enclosingElement} is not a class nor a interface. Only classes or interfaces can be annotated with @${Element::class.simpleName} annotation. If you try to annotate primitives than @${PropertyElement::class.simpleName}")
            }


            if (nameMatchers.isEmpty()) {
                // No polymorphism
                if (isList(element)) {

                    val genericListType = getGenericTypeFromList(element)
                    val genericListTypeElement = typeUtils.asElement(genericListType) as TypeElement

                    if (elementAnnotation.compileTimeChecks) {

                        if (genericListTypeElement.isInterface()) {
                            throw ProcessingException(element, "The generic list type of '$element' in class ${element.getSurroundingClassQualifiedName()} is an interface. Hence polymorphism must be resolved manually by using @${ElementNameMatcher::class.simpleName}.")
                        }

                        if (genericListTypeElement.isAbstract()) {
                            throw ProcessingException(element, "The generic list type of '$element' in class ${element.getSurroundingClassQualifiedName()} is a abstrac class. Hence polymorphism must be resolved manually by using @${ElementNameMatcher::class.simpleName}.")
                        }
                    }

                    val elementName = if (elementAnnotation.name.isEmpty()) {
                        getXmlElementNameOrThrowException(element, genericListTypeElement, elementAnnotation.compileTimeChecks)
                    } else {
                        elementAnnotation.name
                    }

                    return ListElementField(
                            element,
                            elementName,
                            genericListType
                    )

                } else {
                    // A simple element without polymorphism

                    if (elementAnnotation.compileTimeChecks) {

                        // Interfaces not allowed with no name matchers to resolve polymorphism
                        if (typeUtils.asElement(element.asType()).kind == ElementKind.INTERFACE) {
                            throw ProcessingException(element, "The type of field '${element.simpleName}' in class ${element.enclosingElement} is an interface. Since interfaces cannot be instantiated you have to specify which class should be instantiated (resolve polymorphism) manually by @${Element::class.simpleName}( typesByElement = ... )")
                        }

                        // abstract classes not allowed with no name matchers to resolve polymorphism
                        if (typeUtils.asElement(element.asType()).isAbstract()) {
                            throw ProcessingException(element, "The type of field '${element.simpleName}' in class ${element.enclosingElement} is an abstract class. Since abstract classes cannot no be instantiated you have to specify which class should be instantiated (resolve polymorphism) manually by @${Element::class.simpleName}( typesByElement = ... )")
                        }
                    }

                    val elementName = if (elementAnnotation.name.isEmpty()) {
                        getXmlElementNameOrThrowException(element, (element.asType() as DeclaredType).asElement() as TypeElement, elementAnnotation.compileTimeChecks)
                    } else {
                        elementAnnotation.name
                    }

                    return ElementField(
                            element,
                            elementName
                    )
                }
            } else {
                // polymorphism
                if (isList(element)) {

                    val genericListType = getGenericTypeFromList(element)
                    return PolymorphicListElementField(
                            element,
                            "placeHolderToSubstituteWithPolymorphicListElement",
                            getPolymorphicTypes(element, nameMatchers),
                            genericListType
                    )

                } else {
                    return PolymorphicElementField(
                            element,
                            "placeholderTOSubstituteWithPolymorhicElement",
                            getPolymorphicTypes(element, nameMatchers)
                    )
                }
            }

        }


        throw ProcessingException(element, "Unknown annotation detected! I'm sorry, this should not happen. Please file an issue on github https://github.com/Tickaroo/tikxml/issues ")
    }

    /**
     * Checks whether or not thy element is of type (or subtype) java.util.List
     */
    protected fun isList(element: VariableElement): Boolean {
        // return typeUtils.isAssignable(element.asType(), listTypeMirror)
        return element.isList()
    }

    private fun getPolymorphicTypes(element: VariableElement, matcherAnnotations: Array<ElementNameMatcher>): List<PolymorphicTypeElementNameMatcher> {

        if (matcherAnnotations.isEmpty()) {
            throw ProcessingException(element, "No @${ElementNameMatcher::class.simpleName} specified to resolve polymorphism")
        }

        val checkTargetClassXmlAnnotated = fun(typeElement: TypeElement) {
            if (typeElement.getAnnotation(Xml::class.java) == null) {
                throw ProcessingException(element, "The class ${typeElement.qualifiedName} is not annotated with @${Xml::class.simpleName}, but is used in '$element' in class @${element.getSurroundingClassQualifiedName()} to resolve polymorphism. Please annotate @${element.getSurroundingClassQualifiedName()} with @${Xml::class.simpleName}")
            }
        }

        val namingMap = HashMap<String, PolymorphicTypeElementNameMatcher>()

        for (matcher in matcherAnnotations) {
            try {

                val typeClass = matcher.type
                val typeElement = elementUtils.getTypeElement(typeClass.qualifiedName)

                checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(element, typeElement)
                checkTargetClassXmlAnnotated(typeElement)
                val xmlElementName = if (matcher.name.isEmpty()) {
                    getXmlElementName(typeElement)
                } else {
                    matcher.name
                }

                val typeMatcher = namingMap[xmlElementName]
                if (typeMatcher != null) {
                    throw ProcessingException(element, "Conflict: A @${ElementNameMatcher::class.simpleName} with the name \"$xmlElementName\" is already mapped to the type ${typeMatcher.type} to resolve polymorphism. Hence it cannot be mapped to $typeElement as well.")
                } else {
                    namingMap.put(xmlElementName, PolymorphicTypeElementNameMatcher(xmlElementName, typeElement.asType()))
                }

            } catch(mte: MirroredTypeException) {

                val typeMirror = mte.typeMirror
                if (typeMirror.kind != TypeKind.DECLARED) {
                    throw ProcessingException(element, "Only classes can be specified as type in @${ElementNameMatcher::class.simpleName}")
                }

                val typeElement = (typeMirror as DeclaredType).asElement() as TypeElement

                checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(element, typeElement)
                checkTargetClassXmlAnnotated(typeElement)

                val xmlElementName = if (matcher.name.isEmpty()) {
                    getXmlElementName(typeElement)
                } else {
                    matcher.name
                }

                val typeMatcher = namingMap[xmlElementName]
                if (typeMatcher != null) {
                    throw ProcessingException(element, "Conflict: A @${ElementNameMatcher::class.simpleName} with the name \"$xmlElementName\" is already mapped to the type ${typeMatcher.type} to resolve polymorphism. Hence it cannot be mapped to $typeElement as well.")
                } else {
                    namingMap.put(xmlElementName, PolymorphicTypeElementNameMatcher(xmlElementName, typeElement.asType()))
                }
            }

        }

        return ArrayList(namingMap.values)
    }

    /**
     * Checks if a the typeElement is a public class (or default package visibility if in same package as variableElement)
     */
    private fun checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(variableElement: VariableElement, typeElement: TypeElement) {

        if (!typeElement.isClass()) {
            throw ProcessingException(variableElement, "@${ElementNameMatcher::class.simpleName} only allows classes. $typeElement is a not a class!")
        }

        if (typeElement.isPrivate()) {
            throw ProcessingException(variableElement, "@${ElementNameMatcher::class.simpleName} does not allow private classes. $typeElement is a private class!")
        }

        if (typeElement.isProtected()) {
            throw ProcessingException(variableElement, "@${ElementNameMatcher::class.simpleName} does not allow protected classes. $typeElement is a protected class!")
        }

        val hasSamePackage = typeElement.isSamePackageAs(variableElement.enclosingElement, elementUtils)
        val classIsPublic = typeElement.isPublic()

        if (!classIsPublic && !hasSamePackage) {
            throw ProcessingException(variableElement, "@${ElementNameMatcher::class.simpleName} does not allow package visiblity on classes outside of this package. Make $typeElement is a public class or move this class into the same package")
        }

        // Check for subtype
        val variableType = if (isList(variableElement)) getGenericTypeFromList(variableElement) else variableElement.asType()
        if (!typeUtils.isAssignable(typeElement.asType(), variableType)) {
            throw ProcessingException(variableElement, "The type $typeElement must be a sub type of ${variableType}. Otherwise this type cannot be used in @${ElementNameMatcher::class.simpleName} to resolve polymorphism");
        }

        for (constructor in ElementFilter.constructorsIn(typeElement.enclosedElements)) {
            if (constructor.hasEmptyParameters()) {
                if (hasSamePackage and constructor.hasMinimumPackageVisibilityModifiers()) {
                    return
                }

                if (classIsPublic and constructor.isPublic()) {
                    return
                }
            } else if (constructor.parameters.filter { !(it as VariableElement).hasTikXmlAnnotation() }.isEmpty()) {
                return
            }
        }

        
        throw ProcessingException(variableElement, "Class $typeElement used in @${ElementNameMatcher::
        class.simpleName} must provide an public empty (parameter-less) constructor")
    }

    /**
     * Get the genric type of a List
     */
    protected fun getGenericTypeFromList(listVariableElement: VariableElement): TypeMirror {

        if (listVariableElement.asType().kind != TypeKind.DECLARED) {
            throw ProcessingException(listVariableElement, "Element must be of type java.util.List");
        }

        val typeMirror = listVariableElement.asType() as DeclaredType
        when (typeMirror.typeArguments.size) {
            0 -> return elementUtils.getTypeElement("java.lang.Object").asType() // Raw types

            1 -> if (typeMirror.typeArguments[0].kind == TypeKind.WILDCARD) {
                val wildCardMirror = typeMirror.typeArguments[0] as WildcardType
                return if (wildCardMirror.extendsBound != null)
                    wildCardMirror.extendsBound
                else if (wildCardMirror.superBound != null)
                    wildCardMirror.superBound
                else elementUtils.getTypeElement("java.lang.Object").asType() // in case of List<?>
            } else return typeMirror.typeArguments[0]

            else -> throw ProcessingException(listVariableElement, "Seems that you have annotated a List with more than one generic argument? How is this possible?")
        }
    }

    /**
     * Get the name of a annotated field from annotation or use the field name if no name set
     */
    protected fun nameFromAnnotationOrField(name: String, element: VariableElement) =
            if (name.isEmpty()) {
                element.simpleName.toString()
            } else name

    /**
     * Get the xmlElement name which is either @Xml(name = "foo") property or the class name decapitalize (first letter in lower case)
     */
    protected fun getXmlElementNameOrThrowException(field: VariableElement, typeElement: TypeElement, compileTimeChecks: Boolean): String {

        val xmlAnnotation = typeElement.getAnnotation(Xml::class.java)

        if (xmlAnnotation == null && compileTimeChecks) {
            throw ProcessingException(field, "The type ${typeElement.qualifiedName} used for field '$field' in ${field.getSurroundingClassQualifiedName()} can't be used, because is not annotated with @${Xml::class.simpleName}. Annotate ${typeElement.qualifiedName} with @${Xml::class.simpleName}!")
        } else {
            return getXmlElementName(typeElement, xmlAnnotation)
        }
    }

    private fun getXmlElementName(typeElement: TypeElement, xmlAnnotation: Xml? = typeElement.getAnnotation(Xml::class.java)!!) =
            if (xmlAnnotation == null || xmlAnnotation.name.isEmpty()) {
                typeElement.simpleName.toString().decapitalize()
            } else {
                xmlAnnotation.name
            }
}
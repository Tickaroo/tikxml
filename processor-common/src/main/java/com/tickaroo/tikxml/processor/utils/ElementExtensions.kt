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

package com.tickaroo.tikxml.processor.utils

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.TextContent
import java.util.*
import javax.lang.model.element.*
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


/**
 * Checks whether a given element has at least "package" visibility which means, that it is not private nor protected
 */
fun Element.hasMinimumPackageVisibilityModifiers() =
        !isProtected() && !isPrivate()

/**
 * Checks if a method is in the same package as the other
 */
fun Element.isSamePackageAs(other: Element, utils: Elements) = utils.getPackageOf(this) == utils.getPackageOf(other)

/**
 * Checks if a element contains a private modifier
 */
fun Element.isPrivate() = modifiers.contains(Modifier.PRIVATE)

/**
 * Checks if a element has a protected modifier
 */
fun Element.isProtected() = modifiers.contains(Modifier.PROTECTED)

/**
 * Checks if a element has a protected modifier
 */
fun Element.isPublic() = modifiers.contains(Modifier.PUBLIC)

/**
 * Checks if an element has "default" (package) visibility
 */
fun Element.isDefaultVisibility() = !isPrivate() && !isProtected() && !isPublic()

/**
 * Checks if a element has a protected modifier
 */
fun Element.isAbstract() = modifiers.contains(Modifier.ABSTRACT)

/**
 * Checks if the class is an interface
 */
fun Element.isInterface() = kind == ElementKind.INTERFACE

/**
 * Checks if a given element is static
 */
fun Element.isStatic() = modifiers.contains(Modifier.STATIC)

/**
 * Checks if a given element is final
 */
fun Element.isFinal() = modifiers.contains(Modifier.FINAL);

/**
 * Checks if a given element is a field
 */
fun Element.isField() = kind == ElementKind.FIELD

/**
 * Checks if a given element is a class
 */
fun Element.isClass() = kind == ElementKind.CLASS

/**
 * Checks if a given element is a method
 */
fun Element.isMethod() = kind == ElementKind.METHOD

/**
 * Checks if a given element is a constructor
 */
fun Element.isConstructor() = kind == ElementKind.CONSTRUCTOR

/**
 * Checks if a given element has parameters
 */
fun Element.hasEmptyParameters()  =  (this as ExecutableElement).parameters.isEmpty()

/**
 * Checks if a given element is an Empty constructor (visibility not checked)
 */
fun Element.isEmptyConstructor() =
        isConstructor() && hasEmptyParameters()

/**
 * Checks if a given element is empty constructor with minimum package visibility
 */
fun Element.isEmptyConstructorWithMinimumPackageVisibility() = isEmptyConstructor() && hasMinimumPackageVisibilityModifiers()

/**
 * Checks if a given Element is a method with minimum package visibility
 */
fun Element.isMethodWithMinimumPackageVisibility() = isMethod() && hasMinimumPackageVisibilityModifiers()

/**
 * Checks if a given Element is a getter method (prefix = "get" or prefix = "is") with at least one package visibility
 */
fun Element.isGetterMethodWithMinimumPackageVisibility() = isMethodWithMinimumPackageVisibility() && (simpleName.startsWith("get") || simpleName.startsWith("is"))

/**
 * Checks if a given Element is a setted method (prefix = "set") with at least one package visibility
 */
fun Element.isSetterMethodWithMinimumPackageVisibility() = isMethodWithMinimumPackageVisibility() && simpleName.startsWith("set")

/**
 * Checks if a given Element is a setter and has excactly one parameter of the given type
 */
fun Element.isMethodWithOneParameterOfType(type: TypeMirror, typeUtils: Types) =
        isMethod()
                && (this as ExecutableElement).parameters.size == 1
                && typeUtils.isAssignable(parameters[0].asType(), type)

/**
 * Checks if a given Element is a parameterless method
 */
fun Element.isParameterlessMethod() = isMethod() && (this as ExecutableElement).parameters.isEmpty()

fun VariableElement.getSurroundingClass() = when (enclosingElement) {
    is TypeElement -> enclosingElement as TypeElement
    is ExecutableElement -> enclosingElement.enclosingElement as TypeElement
    else -> throw IllegalArgumentException("Unexpected enclosing element $enclosingElement for $this")
}

fun VariableElement.getSurroundingClassQualifiedName() = getSurroundingClass().qualifiedName.toString()

/**
 * Returns true if the element has a super class (breaks if java.lang.Object is the only remaining one.
 */
fun TypeElement.hasSuperClass() = superclass.kind != TypeKind.NONE

/**
 * Get a list of all super classes (exclusive java.lang.Object) by scanning inheritance tree from bottom to top
 */
fun TypeElement.getSuperClasses(typeUtils: Types): List<TypeElement> {

    val superClasses = ArrayList<TypeElement>()
    var current = this
    while (current.hasSuperClass()) {
        current = typeUtils.asElement(current.superclass) as TypeElement
        superClasses.add(current)
    }
    return superClasses
}

/**
 * Get a list of all super classes (exclusive java.lang.Object) by scanning inheritance tree from bottom to top
 * and also includes all interfaces that this class implements or any other super class.
 */
fun TypeElement.getSuperClassesAndAllInterfaces(typeUtils: Types): List<TypeElement> {

    val superClasses = ArrayList<TypeElement>()
    var current = this
    fun addInterfaces(element: TypeElement) {
        if (element.interfaces != null && !element.interfaces.isEmpty()) {
            element.interfaces.forEach {
                superClasses.add(typeUtils.asElement(it) as TypeElement)
            }
        }
    }

    addInterfaces(current)

    while (current.hasSuperClass()) {
        current = typeUtils.asElement(current.superclass) as TypeElement
        superClasses.add(current)
        addInterfaces(current)
    }
    return superClasses
}

/**
 * Checks whether or not this element has at least one TikXml Annotation like @[Attribute]
 */
fun Element.hasTikXmlAnnotation() = getAnnotation(Attribute::class.java) != null
        || getAnnotation(PropertyElement::class.java) != null
        || getAnnotation(com.tickaroo.tikxml.annotation.Element::class.java) != null
        || getAnnotation(TextContent::class.java) != null

/**
 * Checks whether or not a certain element is a list
 */
fun Element.isList() = asType().toString().startsWith("java.util.List");
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

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.text.startsWith


/**
 * Checks whether a given element has at least "package" visibility which means, that it is not private nor protected
 */
fun Element.hasMinimumPackageVisibilityModifiers() =
        !isProtected() && !isPrivate()

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
 * Checks if a element has a protected modifier
 */
fun Element.isAbstract() = modifiers.contains(Modifier.ABSTRACT)


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
 * Checks if a given element is an Empty constructor (visibility not checked)
 */
fun Element.isEmptyConstructor() =
        isConstructor() && (this as ExecutableElement).parameters.isEmpty()

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
fun Element.isGetterMethodWithMinimumPackageVisibility() = isMethodWithMinimumPackageVisibility() && simpleName.startsWith("get") || simpleName.startsWith("is")

/**
 * Checks if a given Element is a setted method (prefix = "set") with at least one package visibility
 */
fun Element.isSetterMethodWithMinimumPackageVisibility() = isMethodWithMinimumPackageVisibility() && simpleName.startsWith("set")

/**
 * Checks if a given Element is a setter and has excactly one parameter of the given type
 */
fun Element.isMethodWithOneParameterOfType(typeUtils: Types) =
        isMethod() && (this as ExecutableElement).parameters.size == 1 && typeUtils.isAssignable(parameters[0].asType(), this.asType())

/**
 * Checks if a given Element is a parameterless method
 */
fun Element.isParameterlessMethod() = isMethod() && (this as ExecutableElement).parameters.isEmpty()
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


/**
 * Checks whether a given element has at least "package" visibility which means, that it is not private nor protected
 */
fun Element.hasMinimumPackageVisibility() =
        !modifiers.contains(Modifier.PRIVATE) && modifiers.contains(Modifier.PROTECTED)

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
fun Element.isEmptyConstructorWithMinimumPackageVisibility() = isEmptyConstructor() && hasMinimumPackageVisibility()
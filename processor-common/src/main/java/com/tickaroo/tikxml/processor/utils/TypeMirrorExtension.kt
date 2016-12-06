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

import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

/**
 * Also treats String as primitive. Includes primitive wrappers
 */
fun TypeMirror.isPrimitiveInclWrapper() = when (kind) {
    TypeKind.BOOLEAN -> true
    TypeKind.CHAR -> true
    TypeKind.DOUBLE -> true
    TypeKind.FLOAT -> true
    TypeKind.INT -> true
    TypeKind.LONG -> true
    else -> when (toString()) {
        "java.lang.Double" -> true
        "java.lang.Integer" -> true
        "java.lang.Boolean" -> true
        "java.lang.Float" -> true
        "java.lang.String" -> true
        "java.lang.Character" -> true
        "java.lang.Long" -> true
        else -> false
    }
}

fun TypeMirror.isPrimitive() = kind.isPrimitive

fun TypeMirror.isBoolean() =
        if (kind == TypeKind.BOOLEAN) true
        else if (toString() == "java.lang.Boolean")
            true
        else false

fun TypeMirror.isString() = when (toString()) {
    "java.lang.String" -> true
    "kotlin.String" -> true
    else -> false

}

fun TypeMirror.isInt() = kind == TypeKind.INT || toString() == "java.lang.Integer" || toString() == "kotlin.Int"

fun TypeMirror.isDouble() = kind == TypeKind.DOUBLE || toString() == "java.lang.Double" || toString() == "kotlin.Double"

fun TypeMirror.isLong() = kind == TypeKind.LONG || toString() == "java.lang.Long" || toString() == "kotlin.Long"
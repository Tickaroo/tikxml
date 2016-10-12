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

package com.tickaroo.tikxml.processor.field.access

import com.squareup.javapoet.CodeBlock
import com.tickaroo.tikxml.processor.generator.CodeGenUtils
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

/**
 * Base class. This class just stores info about how to set / read a a field from annotation processing generated code.
 * (via getter-setter or same field is directly visible)
 * @author Hannes Dorfmann
 */
sealed class FieldAccessPolicy {

    abstract fun resolveAssignment(assignValue: String, argument: Any? = null): CodeBlock

    abstract fun resolveGetter(): String

    abstract fun resolveSetter(): String


    /**
     * Can't access the underlying field directly, hence we need to use the public getter and setter
     */
    class GetterSetterFieldAccessPolicy(private val getter: ExecutableElement, private val setter: ExecutableElement) : FieldAccessPolicy() {

        override fun resolveAssignment(assignValue: String, argument: Any?) =
                CodeBlock.builder()
                        .addStatement("${CodeGenUtils.valueParam}.${setter.simpleName}($assignValue)", argument)
                        .build()

        override fun resolveGetter() =
                "${CodeGenUtils.valueParam}.${getter.simpleName}()"

        override fun resolveSetter() = ""
    }

    /**
     * Policy that can access the field directly because it has at least package visibility
     */
    class MinPackageVisibilityFieldAccessPolicy(private val element: VariableElement) : FieldAccessPolicy() {

        override fun resolveAssignment(assignValue: String, argument: Any?) =
                CodeBlock.builder()
                        .addStatement("${resolveSetter()} = $assignValue", argument)
                        .build()

        override fun resolveGetter() = "${CodeGenUtils.valueParam}.$element"

        override fun resolveSetter() = "${CodeGenUtils.valueParam}.$element"
    }

    /**
     * Specifies that an annotated field is accessible via constructor (to set value) and a getter method to read a value

    class ConstructorAndGetterAccessPolicy(private val getter: ExecutableElement) : FieldAccessPolicy(){

        override fun resolveAssignment(assignValue: String, argument: Any?) =
                CodeBlock.builder()
                        .addStatement("${CodeGenUtils.valueParam}.${setter.simpleName}($assignValue)", argument)
                        .build()

        override fun resolveGetter() =
                "${CodeGenUtils.valueParam}.${getter.simpleName}()"

        override fun resolveSetter() = ""

    }
     */


}
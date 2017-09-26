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
import com.tickaroo.tikxml.processor.generator.CodeGeneratorHelper
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

/**
 * Base class. This class just stores info about how to set / read a a field from annotation processing generated code.
 * (via getter-setter or same field is directly visible)
 * @author Hannes Dorfmann
 */
sealed class FieldAccessResolver {

    abstract fun resolveAssignment(assignValue: String, vararg arguments: Any): CodeBlock

    abstract fun resolveGetterForReadingXml(): String

    abstract fun resolveGetterForWritingXml(): String

    /**
     * Can't access the underlying field directly, hence we need to use the public getter and setter
     */
    class GetterSetterFieldAccessResolver(private val getter: ExecutableElement, private val setter: ExecutableElement) : FieldAccessResolver() {

        override fun resolveAssignment(assignValue: String, vararg arguments: Any) =
                CodeBlock.builder()
                        .addStatement("${CodeGeneratorHelper.valueParam}.${setter.simpleName}($assignValue)", *arguments)
                        .build()

        override fun resolveGetterForReadingXml() =
                "${CodeGeneratorHelper.valueParam}.${getter.simpleName}()"

        override fun resolveGetterForWritingXml() = "${CodeGeneratorHelper.valueParam}.${getter.simpleName}()"
    }

    /**
     * Policy that can access the field directly because it has at least package visibility
     */
    class MinPackageVisibilityFieldAccessResolver(private val element: VariableElement) : FieldAccessResolver() {

        override fun resolveAssignment(assignValue: String, vararg arguments: Any) =
                CodeBlock.builder()
                        .addStatement("${CodeGeneratorHelper.valueParam}.$element = $assignValue", *arguments)
                        .build()

        override fun resolveGetterForReadingXml() = "${CodeGeneratorHelper.valueParam}.$element"

        override fun resolveGetterForWritingXml() = "${CodeGeneratorHelper.valueParam}.$element"
    }

    /**
     * Policy that can access the field directly because it has at least package visibility
     */
    class ConstructorAndGetterFieldAccessResolver(private val element: VariableElement, private val getter: ExecutableElement) : FieldAccessResolver() {

        override fun resolveAssignment(assignValue: String, vararg arguments: Any) =
                CodeBlock.builder()
                        .addStatement("${CodeGeneratorHelper.valueParam}.$element = $assignValue", *arguments)
                        .build()

        override fun resolveGetterForReadingXml() = "${CodeGeneratorHelper.valueParam}.$element"

        override fun resolveGetterForWritingXml() = "${CodeGeneratorHelper.valueParam}.${getter.simpleName}()"
    }

}
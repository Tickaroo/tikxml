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

package com.tickaroo.tikxml.processor.field

import com.tickaroo.tikxml.processor.utils.getSurroundingClassQualifiedName
import javax.lang.model.element.VariableElement

/**
 * Represents a field where we want to parse xml data into or write xml data from.
 *
 * @author Hannes Dorfmann
 */
open class NamedField(
        element: VariableElement,
        val name: String) : Field(element) {


    override fun toString() = "field '${element.simpleName}' in class ${element.getSurroundingClassQualifiedName()}"
}

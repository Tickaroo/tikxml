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

import com.tickaroo.tikxml.processor.model.NamedField
import com.tickaroo.tikxml.processor.model.TextContentField
import javax.lang.model.element.VariableElement

/**
 * Provides a method to specify that a given field is mapped from xml document to java class and vice versa
 * @author Hannes Dorfmann
 */
interface FieldDetectorStrategy {

    /**
     * Checks if the given field should be mapped from xml document (write as xml or read from xml input),
     * Don't check for [com.tickaroo.tikxml.annotation.TextContent] annotation in this call.
     * @see isXmlTextContent
     */
    fun isXmlField(element: VariableElement): NamedField?

    /**
     * Is the field an [com.tickaroo.tikxml.annotation.TextContent] field?
     */
    fun isXmlTextContent(element: VariableElement): TextContentField?
}
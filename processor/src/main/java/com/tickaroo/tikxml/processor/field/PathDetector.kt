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

import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.utils.getSurroundingClassQualifiedName
import javax.lang.model.element.VariableElement
import kotlin.collections.emptyList
import kotlin.collections.forEachIndexed
import kotlin.text.Regex
import kotlin.text.isEmpty
import kotlin.text.matches
import kotlin.text.split

/**
 * This class is responsible to interprete a [com.tickaroo.tikxml.annotation.Path] annotation
 * @author Hannes Dorfmann
 * @since 1.0
 */
object PathDetector {

    private val PATH_SEGMENT_DIVIDER = '/'
    private val SEGMENT_REGEX = Regex("[\\w:]+")


    fun extractPathSegments(element: VariableElement, pathAsString: String): List<String> {

        val segments = pathAsString.split(PATH_SEGMENT_DIVIDER)

        segments.forEachIndexed { index, it ->
            if (!it.matches(SEGMENT_REGEX)) {
                throw ProcessingException(element, "The field '$element' in class ${element.getSurroundingClassQualifiedName()} annotated with @${Path::class.simpleName}(\"$pathAsString\") has an illegal path: Error in path segment '$it' (segment index = $index)")
            }
        }

        return segments
    }

    /**
     * Scans a variable element for [Path] annotation and returns a list of path segments or empty list if no such annotation has been found
     */
    fun getSegments(element: VariableElement): List<String> {

        val annotation = element.getAnnotation(Path::class.java)
        return if (annotation != null) {
            extractPathSegments(element, annotation.value)
        } else {
            emptyList()
        }
    }

}
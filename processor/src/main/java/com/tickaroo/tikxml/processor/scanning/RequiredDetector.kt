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

import com.tickaroo.tikxml.annotation.Required
import javax.lang.model.element.VariableElement


/**
 * Checks if a mapping from xml to java class is required or not
 *
 * @author Hannes Dorfmann
 */
interface RequiredDetector {

    /**
     * Checks if a given field is required or not.
     *
     */
    fun isRequired(element: VariableElement): Boolean?

}

/**
 * Default implementation that checks if a variable Field is annotated with [com.tickaroo.tikxml.annotation.Required] annotation
 * @author Hannes Dorfmann
 */
class AnnotationBasedRequiredDetector : RequiredDetector {

    override fun isRequired(element: VariableElement): Boolean? {
        val annotation = element.getAnnotation(Required::class.java)

        if (annotation != null) {
            return annotation.value
        }

        return null
    }
}
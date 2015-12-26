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

import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.model.AnnotatedClass
import com.tickaroo.tikxml.processor.utils.isEmptyConstructorWithMinimumPackageVisibility
import kotlin.collections.forEach

/**
 * Scans an [com.tickaroo.tikxml.annotation.Xml] annotated class and fulfills the  [AnnotatedClass]
 * @author Hannes Dorfmann
 * @since 1.0
 */
abstract class ScanStrategy {

    /**
     * Scans the child element of the passed [AnnotatedClass] to find [com.tickaroo.tikxml.processor.model.Field]
     */
    @Throws(ProcessingException::class)
    fun scan(annotatedClass: AnnotatedClass) {

        var constructorFound = false

        annotatedClass.element.enclosedElements.forEach { childElement ->

            if (childElement.isEmptyConstructorWithMinimumPackageVisibility()) {
                constructorFound = true
            }

        }


        if (!constructorFound) {
            throw ProcessingException(annotatedClass.element, "${annotatedClass.qualifiedClassName} " +
                    "must provide an empty (parameterless) constructor with minimum default (package) visibility")
        }
    }

}



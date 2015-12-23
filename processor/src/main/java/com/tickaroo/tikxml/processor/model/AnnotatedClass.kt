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
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.tickaroo.tikxml.processor.model

import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.ProcessingException
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

/**
 * This class holds the information of an element that has been annotated with @Xml
 * @author Hannes Dorfmann
 */
class AnnotatedClass
@Throws(ProcessingException::class) constructor(e: Element) {

    val element: TypeElement


    init {
        checkValidClass(e)
        element = e as TypeElement
    }

    private fun checkValidClass(element: Element) {

        if (element.kind != ElementKind.CLASS) {
            throw ProcessingException(element, "Only classes can be annotated with @${Xml::class.simpleName}")
        }
    }

}
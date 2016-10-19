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

import com.tickaroo.tikxml.processor.field.AnnotatedClass
import com.tickaroo.tikxml.processor.field.AttributeField
import com.tickaroo.tikxml.processor.field.Namespace
import com.tickaroo.tikxml.processor.field.TextContentField
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import org.mockito.Mockito
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 *
 * @author Hannes Dorfmann
 */
class MockAnnotatedClass(override val element: TypeElement, override val inheritance: Boolean = true, override val nameAsRoot: String = "", override val simpleClassName: String = "", override val qualifiedClassName: String = "") : AnnotatedClass {

    constructor() : this(Mockito.mock(TypeElement::class.java) as TypeElement)

    override var textContentField: TextContentField? = null

    override val attributes: Map<String, AttributeField> = HashMap()
    override val childElements: Map<String, XmlChildElement> = HashMap()

    override fun isXmlElementAccessableFromOutsideTypeAdapter(): Boolean = true

    override var annotatedConstructor: ExecutableElement? = null;

    override val writeNamespaces: List<Namespace> = emptyList()
}
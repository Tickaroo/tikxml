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

import com.tickaroo.tikxml.annotation.ScanMode
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import com.tickaroo.tikxml.processor.xml.XmlRootElement
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

/**
 * This class holds the information of an element that has been annotated with @Xml
 * @author Hannes Dorfmann
 */
interface AnnotatedClass : XmlRootElement {

    override val element: TypeElement
    override val nameAsRoot: String
    val scanMode: ScanMode
    val inheritance: Boolean
    val simpleClassName: String
    val qualifiedClassName: String

    var textContentField: TextContentField?

}

/**
 * Concrete [AnnotatedClass] implementation
 * @author Hannes Dorfmann
 */
class AnnotatedClassImpl
@Throws(ProcessingException::class) constructor(e: Element) : AnnotatedClass {

    override val attributes = HashMap<String, AttributeField>()
    override val childElements = HashMap<String, XmlChildElement>()

    override val element: TypeElement

    override val scanMode: ScanMode
    override val inheritance: Boolean
    override val nameAsRoot: String
    override val simpleClassName: String
    override val qualifiedClassName: String

    override var textContentField: TextContentField? = null

    init {
        checkValidClass(e)
        element = e as TypeElement

        simpleClassName = element.simpleName.toString()
        qualifiedClassName = element.qualifiedName.toString()

        val xmlAnnotation = element.getAnnotation(Xml::class.java)
        scanMode = xmlAnnotation.scanMode
        inheritance = xmlAnnotation.inheritance
        nameAsRoot = xmlAnnotation.nameAsRoot

    }

    private fun checkValidClass(element: Element) {

        if (element.kind != ElementKind.CLASS) {
            throw ProcessingException(element, "Only classes can be annotated with " +
                    "@${Xml::class.simpleName} but ${element.toString()} is not a class")


        }
    }

    override fun isXmlElementAccessableFromOutsideTypeAdapter(): Boolean = true

    override fun hasTextContent() = textContentField != null
}
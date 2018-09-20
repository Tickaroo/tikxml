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

import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.XmlCharacters
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import com.tickaroo.tikxml.processor.xml.XmlRootElement
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import kotlin.collections.LinkedHashMap

/**
 * This class holds the information of an element that has been annotated with @Xml
 * @author Hannes Dorfmann
 */
interface AnnotatedClass : XmlRootElement {

    override val element: TypeElement
    override val nameAsRoot: String
    val inheritance: Boolean
    val simpleClassName: String
    val qualifiedClassName: String
    var textContentField: TextContentField?
    var annotatedConstructor: ExecutableElement?
    val writeNamespaces: List<Namespace>
}

/**
 * Concrete [AnnotatedClass] implementation
 * @author Hannes Dorfmann
 */
class AnnotatedClassImpl
@Throws(ProcessingException::class) constructor(e: Element) : AnnotatedClass {

    override val attributes = LinkedHashMap<String, AttributeField>()
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override val element: TypeElement

    override val inheritance: Boolean
    override val nameAsRoot: String
    override val simpleClassName: String
    override val qualifiedClassName: String
    override var annotatedConstructor: ExecutableElement? = null; // TODO implement that

    override var textContentField: TextContentField? = null
    override val writeNamespaces: List<Namespace>

    init {
        checkValidClass(e)
        element = e as TypeElement

        simpleClassName = element.simpleName.toString()
        qualifiedClassName = element.qualifiedName.toString()

        val xmlAnnotation = element.getAnnotation(Xml::class.java)
        inheritance = xmlAnnotation.inheritance

        nameAsRoot =
                if (xmlAnnotation.name.isEmpty()) {
                    if (simpleClassName.length <= 1) {
                        simpleClassName.decapitalize()
                    } else {
                        simpleClassName[0].toLowerCase() + simpleClassName.substring(1)
                    }
                } else {
                    xmlAnnotation.name
                }

        if (xmlAnnotation.writeNamespaces.isEmpty()) {
            writeNamespaces = emptyList()
        } else {
            val namespaces = ArrayList<Namespace>();
            for (namespace in xmlAnnotation.writeNamespaces) {
                // check if namespace definition is valid
                if (XmlCharacters.containsXmlCharacter(namespace))
                    throw ProcessingException(element, "@${Xml::class.simpleName} annotated class $simpleClassName contains an illegal namespace definition $namespace . The following characters are not allowed: < > \" ' to be used in a namespace definition");

                val parts = namespace.split("=")
                if (parts.size == 1)
                    namespaces.add(Namespace.DefaultNamespace(namespace))
                else if (parts.size == 2)
                    namespaces.add(Namespace.PrefixedNamespace(parts[0], parts[1]))
                else throw ProcessingException(element, "@${Xml::class.simpleName} annotated class $simpleClassName contains an illegal namespace definition: $namespace because it contains more than 1 equals sign (=) character")

            }

            writeNamespaces = namespaces
        }

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

sealed class Namespace(val uri: String) {
    class PrefixedNamespace(val prefix: String, uri: String) : Namespace(uri)
    class DefaultNamespace(uri: String) : Namespace(uri)
}

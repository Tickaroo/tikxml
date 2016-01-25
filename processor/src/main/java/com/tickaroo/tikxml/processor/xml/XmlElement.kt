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

package com.tickaroo.tikxml.processor.xml

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.field.AttributeField

/**
 *
 * @author Hannes Dorfmann
 */
interface XmlElement {

    val element: javax.lang.model.element.Element

    /**
     * Immutable to the outside, however should be mutable for the inside
     */
    val attributes: Map<String, AttributeField>
    val childElements: Map<String, XmlChildElement>

    fun hasAttributes() = attributes.isNotEmpty()

    fun hasChildElements() = childElements.isNotEmpty()

    fun hasTextContent() = false

    /**
     * Can this [XmlElement] be merged with another [XmlElement] ?
     * For instance a [PlaceholderXmlElement] can be merged with [com.tickaroo.tikxml.processor.field.PropertyField]
     * because his xml child elements and xml attributes can be parse within the root elements TypeAdapter,
     * whereas [com.tickaroo.tikxml.processor.field.ElementField] will be parsed in his own TypeAdapter
     * and therefore the xml attributes and child elements are not parsed by the root elements TypeAdapter,
     * but rather by the TypeAdapter generated for the corresponding [com.tickaroo.tikxml.processor.field.ElementField].
     * Thus, [com.tickaroo.tikxml.processor.field.ElementField] is not mergeable.
     */
    fun isXmlElementAccessableFromOutsideTypeAdapter(): Boolean

    /**
     * Get the element at the given path
     */
    fun getXmlElementForPath(path: List<String>): XmlElement {

        var currentElement = this

        for (segment in path) {
            val childElement = currentElement.childElements[segment]

            if (childElement == null) {

                // Ugly hack! Can't think of a better alterniative right now
                val placeholderElement = PlaceholderXmlElement(segment, currentElement.element)

                (currentElement.childElements as MutableMap).put(segment, placeholderElement)
                currentElement = placeholderElement
            } else {
                currentElement = childElement
            }
        }

        return currentElement
    }

    /**
     * Add an Attribute to the given class
     */
    fun addAttribute(attributeField: AttributeField, path: List<String>) {

        val currentElement = getXmlElementForPath(path)
        if (!currentElement.isXmlElementAccessableFromOutsideTypeAdapter()) {
            throw ProcessingException(currentElement.element, "Element $currentElement can't have attributes that are accessed from outside of the TypeAdapter that is generated from @${Element::class.simpleName} annotated class! Therefore attribute $attributeField can't be added. Most likely the @${Path::class.simpleName} is in conflict with an @${Element::class.simpleName} annotation.")
        }

        val existingAttribute = currentElement.attributes[attributeField.name]

        if (existingAttribute != null) {
            throw ProcessingException(attributeField.element, "Conflict: $attributeField has the same xml attribute name "
                    + "'${attributeField.name}' as the $existingAttribute. "
                    + "You can specify another name via annotations.")
        } else {
            (currentElement.attributes as MutableMap).put(attributeField.name, attributeField);
        }

    }

    /**
     * Adds a child element at the given path
     */
    fun addChildElement(toInsert: XmlChildElement, path: List<String>) {

        val currentElement = getXmlElementForPath(path)

        val existingElement = currentElement.childElements[toInsert.name]
        if (existingElement != null) {

            if (toInsert.isXmlElementAccessableFromOutsideTypeAdapter() && existingElement.isXmlElementAccessableFromOutsideTypeAdapter() && existingElement is PlaceholderXmlElement) {
                mergeXmlElements(currentElement, existingElement, toInsert)
            } else {
                // Conflict
                val variableField = toInsert.element
                throw ProcessingException(variableField, "Conflict: $toInsert is in conflict with $existingElement. Maybe both have the same xml name '${toInsert.name}' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
            }

        } else {
            (currentElement.childElements as MutableMap).put(toInsert.name, toInsert)
        }

    }

    private fun mergeXmlElements(parentElement: XmlElement, toMerge: XmlChildElement, into: XmlChildElement) {

        // merge attributes
        for ((name, attributeField) in toMerge.attributes) {
            if (into.attributes[name] != null) {
                // Should not be possible, because we can't build a path with two PlaceholderXmlElement. Hence, this should never be reached.
                throw ProcessingException(toMerge.element, "Conflict: $toMerge has the same xml attribute name '$name' as $into . You can specify another name via annotations.")
            }

            (into.attributes as MutableMap).put(name, attributeField)
        }

        // merge child elements
        for ((name, element) in toMerge.childElements) {
            if (into.childElements[name] != null) {
                // Should not be possible, because we can't build a path with two placeholder PlaceholderXmlElement. Hence, this should never be reached.
                throw ProcessingException(toMerge.element, "Conflict: $toMerge is in conflict with $into. Maybe both have the same xml name '$name' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.")
            }

            (into.childElements as MutableMap).put(name, element)
        }

        (parentElement.childElements as MutableMap).put(into.name, into)
    }
}
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
import com.tickaroo.tikxml.processor.expectException
import com.tickaroo.tikxml.processor.field.AttributeField
import com.tickaroo.tikxml.processor.field.ElementField
import com.tickaroo.tikxml.processor.field.PropertyField
import com.tickaroo.tikxml.processor.mock.MockVariableElement
import com.tickaroo.tikxml.processor.mock.MockXmlElement
import org.junit.Test
import kotlin.test.assertTrue

/**
 *
 * @author Hannes Dorfmann
 */
class XmlElementTest {

    @Test
    fun addAttribute() {

        val xmlElement = MockXmlElement();
        val attribute1 = AttributeField(MockVariableElement("foo"), "foo")
        val attribute2 = AttributeField(MockVariableElement("other"), "other")

        xmlElement.addAttribute(attribute1, emptyList())
        assertTrue(xmlElement.attributes["foo"] == attribute1)


        xmlElement.addAttribute(attribute2, emptyList())
        assertTrue(xmlElement.attributes["other"] == attribute2)
    }

    @Test
    fun addAttributeInConflict() {

        val xmlElement = MockXmlElement();
        val attribute1 = AttributeField(MockVariableElement("foo"), "foo")
        val attribute2 = AttributeField(MockVariableElement("other"), "foo")
        val path = emptyList<String>()

        xmlElement.addAttribute(attribute1, path)
        assertTrue(xmlElement.attributes["foo"] == attribute1)

        expectException("Conflict: field 'other' in class mocked.MockedClass has the same xml attribute name 'foo' as the field 'foo' in class mocked.MockedClass. You can specify another name via annotations.") {
            xmlElement.addAttribute(attribute2, path)
        }
    }

    @Test
    fun noAttributeConflictBecauseDifferentPath() {

        val xmlElement = MockXmlElement();
        val attribute1 = AttributeField(MockVariableElement("foo"), "foo")
        val attribute2 = AttributeField(MockVariableElement("other"), "foo")
        val path1 = emptyList<String>()
        val path2 = arrayListOf("some")

        xmlElement.addAttribute(attribute1, path1)
        assertTrue(xmlElement.attributes["foo"] == attribute1)

        xmlElement.addAttribute(attribute2, path2)
        assertTrue(xmlElement.getXmlElementForPath(path2).attributes["foo"] == attribute2)
    }

    @Test
    fun noConflictPropertyElements() {
        val rootElement = MockXmlElement()

        val childElement1 = PropertyField(MockVariableElement("foo"), "foo")
        val childElement2 = PropertyField(MockVariableElement("other"), "other")

        val path = emptyList<String>()

        rootElement.addChildElement(childElement1, path)
        assertTrue(childElement1 == rootElement.childElements["foo"])

        rootElement.addChildElement(childElement2, path)
        assertTrue(childElement2 == rootElement.childElements["other"])

    }

    @Test
    fun noConflictPropertyElementsDifferentPath() {
        val rootElement = MockXmlElement()

        val childElement1 = PropertyField(MockVariableElement("foo"), "foo")
        val childElement2 = PropertyField(MockVariableElement("foo"), "foo")

        val path1 = listOf("a")
        val path2 = listOf("a", "b")

        rootElement.addChildElement(childElement1, path1)
        assertTrue(childElement1 == rootElement.getXmlElementForPath(path1).childElements["foo"])

        rootElement.addChildElement(childElement2, path2)
        assertTrue(childElement2 == rootElement.getXmlElementForPath(path2).childElements["foo"])

    }

    @Test
    fun conflictingProperties() {
        val rootElement = MockXmlElement()

        val childElement1 = PropertyField(MockVariableElement("foo"), "foo")
        val childElement2 = PropertyField(MockVariableElement("foo"), "foo")

        val path1 = listOf("a")
        val path2 = listOf("a")

        rootElement.addChildElement(childElement1, path1)
        assertTrue(childElement1 == rootElement.getXmlElementForPath(path1).childElements["foo"])

        expectException("Conflict: field 'foo' in class mocked.MockedClass is in conflict with field 'foo' in class mocked.MockedClass. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.") {
            rootElement.addChildElement(childElement2, path2)
        }
    }

    @Test
    fun addingAttributeToExistingNode() {
        val rootElement = MockXmlElement()

        val attribute1 = AttributeField(MockVariableElement("attribute1"), "attribute1")
        val childElement1 = PropertyField(MockVariableElement("foo"), "foo")

        val path1 = listOf("a")

        rootElement.addAttribute(attribute1, path1)
        assertTrue(rootElement.getXmlElementForPath(path1) is PlaceholderXmlElement)
        assertTrue(rootElement.getXmlElementForPath(path1).attributes["attribute1"] == attribute1)

        rootElement.addChildElement(childElement1, path1)
        assertTrue(rootElement.getXmlElementForPath(path1) is PlaceholderXmlElement)
        assertTrue(rootElement.getXmlElementForPath(path1.plus("foo")) == childElement1)
        assertTrue(rootElement.getXmlElementForPath(path1).attributes["attribute1"] == attribute1)

        // Add an attribute on existing PropertyField
        val attribute2 = AttributeField(MockVariableElement("attribute2"), "attribute2")
        val path2 = listOf("a", "foo") // Add child on childElement1
        rootElement.addAttribute(attribute2, path2)
        assertTrue(rootElement.getXmlElementForPath(path2) == childElement1)

        assertTrue(rootElement.getXmlElementForPath(path2).attributes["attribute2"] == attribute2)
        assertTrue(childElement1.attributes["attribute2"] == attribute2)

    }

    @Test
    fun mergePlaceholderFromAttributeWithPropertyNode() {
        val rootElement = MockXmlElement()
        val attribute = AttributeField(MockVariableElement("attribute1"), "attribute1")
        val childElement = PropertyField(MockVariableElement("foo"), "foo")

        val attributePath = listOf("foo")

        rootElement.addAttribute(attribute, attributePath)
        assertTrue(rootElement.getXmlElementForPath(attributePath) is PlaceholderXmlElement)
        rootElement.addChildElement(childElement, emptyList()) // Add foo node
        assertTrue(rootElement.getXmlElementForPath(attributePath) == childElement)
        assertTrue(rootElement.getXmlElementForPath(attributePath).attributes["attribute1"] == attribute)
        assertTrue(childElement.attributes["attribute1"] == attribute)

    }

    @Test
    fun mergingPlaceholderFromAttributeWithElementFieldNotPossible() {
        val rootElement = MockXmlElement()
        val attribute = AttributeField(MockVariableElement("attribute1"), "attribute1")
        val childElement = ElementField(MockVariableElement("foo"), "foo")

        val attributePath = listOf("foo")

        rootElement.addAttribute(attribute, attributePath)
        assertTrue(rootElement.getXmlElementForPath(attributePath) is PlaceholderXmlElement)

        expectException ("Conflict: field 'foo' in class mocked.MockedClass is in conflict with mocked.MockedClass. Maybe both have the same xml name 'foo' (you can change that via annotations) or @${Path::class.simpleName} is causing this conflict.") {
            rootElement.addChildElement(childElement, emptyList()) // Add foo node
        }
    }

    @Test
    fun addingAttributeToElementFieldNotPossible() {
        val rootElement = MockXmlElement()
        val attribute = AttributeField(MockVariableElement("attribute1"), "attribute1")
        val childElement = ElementField(MockVariableElement("foo"), "foo")

        val attributePath = listOf("foo")

        rootElement.addChildElement(childElement, emptyList()) // Add foo node


        // Add attribute to foo node should fail
        expectException("Element field 'foo' in class mocked.MockedClass can't have attributes that are accessed from outside of the TypeAdapter that is generated from @${Element::class.simpleName} annotated class! Therefore attribute field 'attribute1' in class mocked.MockedClass can't be added. Most likely the @${Path::class.simpleName} is in conflict with an @${Element::class.simpleName} annotation.") {
            rootElement.addAttribute(attribute, attributePath)
        }

    }

    @Test
    fun attributeInSamePath() {
        val rootElement = MockXmlElement()
        val attribute1 = AttributeField(MockVariableElement("attribute1"), "foo")
        val attribute2 = AttributeField(MockVariableElement("attribute2"), "foo")

        val path = listOf("a", "b")

        rootElement.addAttribute(attribute1, path)


        // Add attribute to foo node should fail
        expectException("Conflict: field 'attribute2' in class mocked.MockedClass has the same xml attribute name 'foo' as the field 'attribute1' in class mocked.MockedClass. You can specify another name via annotations.") {
            rootElement.addAttribute(attribute2, path)
        }

    }
}

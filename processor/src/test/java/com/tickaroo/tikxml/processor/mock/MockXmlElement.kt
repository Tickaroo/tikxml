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

package com.tickaroo.tikxml.processor.mock

import com.tickaroo.tikxml.processor.field.AttributeField
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import com.tickaroo.tikxml.processor.xml.XmlRootElement
import java.util.*
import javax.lang.model.element.TypeElement

/**
 *
 * @author Hannes Dorfmann
 */
class MockXmlElement(override val nameAsRoot: String = "mockRoot", override val element: TypeElement = MockClassElement()) : XmlRootElement {

    override val attributes = HashMap<String, AttributeField>()
    override val childElements = HashMap<String, XmlChildElement>()

    override fun isXmlElementAccessableFromOutsideTypeAdapter() = true

}
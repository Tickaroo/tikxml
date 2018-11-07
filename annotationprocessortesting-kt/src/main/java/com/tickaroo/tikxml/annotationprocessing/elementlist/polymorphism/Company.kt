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

package com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Company {

    @PropertyElement
    var name: String? = null

    @Element(typesByElement = [
        ElementNameMatcher(type = Boss::class),
        ElementNameMatcher(type = Employee::class)
    ])
    var persons: List<@JvmSuppressWildcards Person>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Company) return false

        val company = other as Company?

        if (if (name != null) name != company!!.name else company!!.name != null) return false
        return if (persons != null) persons == company.persons else company.persons == null
    }

    override fun hashCode(): Int {
        var result = if (name != null) name!!.hashCode() else 0
        result = 31 * result + if (persons != null) persons!!.hashCode() else 0
        return result
    }
}

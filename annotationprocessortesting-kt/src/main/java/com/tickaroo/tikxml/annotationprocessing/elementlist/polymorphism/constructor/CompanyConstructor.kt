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

package com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism.constructor

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism.Person

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "company")
class CompanyConstructor(
        @param:PropertyElement val name: String?,
        @param:Element(typesByElement = [
            ElementNameMatcher(type = BossConstructor::class, name = "boss"),
            ElementNameMatcher(type = EmployeeConstructor::class, name = "employee")
        ])
        val persons: List<@JvmSuppressWildcards Person>?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompanyConstructor) return false

        val that = other as CompanyConstructor?

        if (if (name != null) name != that!!.name else that!!.name != null) return false
        return if (persons != null) persons == that.persons else that.persons == null
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (persons?.hashCode() ?: 0)
        return result
    }
}

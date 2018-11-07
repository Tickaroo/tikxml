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

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism.Person

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "boss")
class BossConstructor(@param:Attribute val firstName: String?, @param:Attribute val lastName: String?) : Person {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BossConstructor) return false

        val that = other as BossConstructor?

        if (if (firstName != null) firstName != that!!.firstName else that!!.firstName != null)
            return false
        return if (lastName != null) lastName == that.lastName else that.lastName == null
    }

    override fun hashCode(): Int {
        var result = firstName?.hashCode() ?: 0
        result = 31 * result + (lastName?.hashCode() ?: 0)
        return result
    }
}

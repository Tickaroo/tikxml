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

package com.tickaroo.tikxml.annotationprocessing.element.polymorphism

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Paper {

    @Element(typesByElement = [
        ElementNameMatcher(type = Journalist::class),
        ElementNameMatcher(type = Organisation::class)
    ])
    var writer: Writer? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Paper) return false

        val paper = other as Paper?

        return if (writer != null) writer == paper!!.writer else paper!!.writer == null
    }

    override fun hashCode(): Int {
        return if (writer != null) writer!!.hashCode() else 0
    }
}

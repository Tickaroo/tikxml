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

package com.tickaroo.tikxml.annotationprocessing.elementlist

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "catalogue")
class InlineListCatalogue {

    @Element(name = "book")
    var books: List<Book>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InlineListCatalogue) return false

        val that = other as InlineListCatalogue?

        return if (books != null) books == that!!.books else that!!.books == null
    }

    override fun hashCode(): Int {
        return if (books != null) books!!.hashCode() else 0
    }
}

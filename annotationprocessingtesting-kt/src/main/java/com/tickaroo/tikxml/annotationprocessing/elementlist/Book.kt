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

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml
class Book {

    @Attribute
    var id: Int = 0
    @PropertyElement
    var author: String? = null
    @PropertyElement
    var title: String? = null
    @PropertyElement
    var genre: String? = null
    @PropertyElement(name = "publish_date", converter = DateConverter::class)
    var publishDate: Date? = null
    @PropertyElement
    var price: Double = 0.toDouble()
    @PropertyElement
    var description: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Book) return false

        val book = other as Book?

        if (id != book!!.id) return false
        if (java.lang.Double.compare(book.price, price) != 0) return false
        if (if (author != null) author != book.author else book.author != null) return false
        if (if (title != null) title != book.title else book.title != null) return false
        if (if (genre != null) genre != book.genre else book.genre != null) return false
        if (if (publishDate != null) publishDate != book.publishDate else book.publishDate != null) {
            return false
        }
        return if (description != null) description == book.description else book.description == null
    }

    override fun hashCode(): Int {
        var result: Int
        val temp: Long
        result = id
        result = 31 * result + if (author != null) author!!.hashCode() else 0
        result = 31 * result + if (title != null) title!!.hashCode() else 0
        result = 31 * result + if (genre != null) genre!!.hashCode() else 0
        result = 31 * result + if (publishDate != null) publishDate!!.hashCode() else 0
        temp = java.lang.Double.doubleToLongBits(price)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        result = 31 * result + if (description != null) description!!.hashCode() else 0
        return result
    }
}

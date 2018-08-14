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

package com.tickaroo.tikxml.annotationprocessing.attribute

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
class ItemWithGetterSetters {

    @Attribute
    var aString: String? = null

    @Attribute
    var anInt: Int = 0

    @Attribute
    var aBoolean: Boolean = false

    @Attribute
    var aDouble: Double = 0.toDouble()

    @Attribute
    var aLong: Long = 0

    @Attribute(converter = DateConverter::class)
    var aDate: Date? = null

    @Attribute
    var intWrapper: Int? = null

    @Attribute
    var booleanWrapper: Boolean? = null

    @Attribute
    var doubleWrapper: Double? = null

    @Attribute
    var longWrapper: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemWithGetterSetters) return false

        val that = other as ItemWithGetterSetters?

        if (anInt != that!!.anInt) return false
        if (aBoolean != that.aBoolean) return false
        if (java.lang.Double.compare(that.aDouble, aDouble) != 0) return false
        if (aLong != that.aLong) return false
        if (if (aString != null) aString != that.aString else that.aString != null) return false
        if (if (aDate != null) aDate != that.aDate else that.aDate != null) return false
        if (if (intWrapper != null) intWrapper != that.intWrapper else that.intWrapper != null) {
            return false
        }
        if (if (booleanWrapper != null)
                    booleanWrapper != that.booleanWrapper
                else
                    that.booleanWrapper != null) {
            return false
        }
        if (if (doubleWrapper != null)
                    doubleWrapper != that.doubleWrapper
                else
                    that.doubleWrapper != null) {
            return false
        }
        return if (longWrapper != null) longWrapper == that.longWrapper else that.longWrapper == null
    }

    override fun hashCode(): Int {
        var result: Int
        val temp: Long
        result = if (aString != null) aString!!.hashCode() else 0
        result = 31 * result + anInt
        result = 31 * result + if (aBoolean) 1 else 0
        temp = java.lang.Double.doubleToLongBits(aDouble)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        result = 31 * result + (aLong xor aLong.ushr(32)).toInt()
        result = 31 * result + if (aDate != null) aDate!!.hashCode() else 0
        result = 31 * result + if (intWrapper != null) intWrapper!!.hashCode() else 0
        result = 31 * result + if (booleanWrapper != null) booleanWrapper!!.hashCode() else 0
        result = 31 * result + if (doubleWrapper != null) doubleWrapper!!.hashCode() else 0
        result = 31 * result + if (longWrapper != null) longWrapper!!.hashCode() else 0
        return result
    }
}

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
@Xml
class Item {
    @Attribute
    @JvmField
    var aString: String? = null

    @Attribute
    @JvmField
    var anInt: Int = 0

    @Attribute
    @JvmField
    var aBoolean: Boolean = false

    @Attribute
    @JvmField
    var aDouble: Double = 0.toDouble()

    @Attribute
    @JvmField
    var aLong: Long = 0

    @Attribute(converter = DateConverter::class)
    @JvmField
    var aDate: Date? = null

    @Attribute
    @JvmField
    var intWrapper: Int? = null

    @Attribute
    @JvmField
    var booleanWrapper: Boolean? = null

    @Attribute
    @JvmField
    var doubleWrapper: Double? = null

    @Attribute
    @JvmField
    var longWrapper: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Item) return false

        val item = other as Item?

        if (anInt != item!!.anInt) return false
        if (aBoolean != item.aBoolean) return false
        if (java.lang.Double.compare(item.aDouble, aDouble) != 0) return false
        if (aLong != item.aLong) return false
        if (if (aString != null) aString != item.aString else item.aString != null) return false
        if (if (aDate != null) aDate != item.aDate else item.aDate != null) return false
        if (if (intWrapper != null) intWrapper != item.intWrapper else item.intWrapper != null) {
            return false
        }
        if (if (booleanWrapper != null)
                    booleanWrapper != item.booleanWrapper
                else
                    item.booleanWrapper != null) {
            return false
        }
        if (if (doubleWrapper != null)
                    doubleWrapper != item.doubleWrapper
                else
                    item.doubleWrapper != null) {
            return false
        }
        return if (longWrapper != null) longWrapper == item.longWrapper else item.longWrapper == null
    }

    override fun hashCode(): Int {
        var result: Int = if (aString != null) aString!!.hashCode() else 0
        val temp: Long = java.lang.Double.doubleToLongBits(aDouble)
        result = 31 * result + anInt
        result = 31 * result + if (aBoolean) 1 else 0
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

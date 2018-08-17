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

package com.tickaroo.tikxml.annotationprocessing.propertyelement.constructor

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
class PropertyItemConstructor(
        @param:PropertyElement val aString: String?,
        @param:PropertyElement val anInt: Int,
        @param:PropertyElement val aBoolean: Boolean,
        @param:PropertyElement val aDouble: Double,
        @param:PropertyElement val aLong: Long,
        @param:PropertyElement(converter = DateConverter::class) val aDate: Date?,
        @param:PropertyElement val intWrapper: Int?,
        @param:PropertyElement val booleanWrapper: Boolean?,
        @param:PropertyElement val doubleWrapper: Double?,
        @param:PropertyElement val longWrapper: Long?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PropertyItemConstructor) return false

        val that = other as PropertyItemConstructor?

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
        result = aString?.hashCode() ?: 0
        result = 31 * result + anInt
        result = 31 * result + if (aBoolean) 1 else 0
        temp = java.lang.Double.doubleToLongBits(aDouble)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        result = 31 * result + (aLong xor aLong.ushr(32)).toInt()
        result = 31 * result + (aDate?.hashCode() ?: 0)
        result = 31 * result + (intWrapper?.hashCode() ?: 0)
        result = 31 * result + (booleanWrapper?.hashCode() ?: 0)
        result = 31 * result + (doubleWrapper?.hashCode() ?: 0)
        result = 31 * result + (longWrapper?.hashCode() ?: 0)
        return result
    }
}

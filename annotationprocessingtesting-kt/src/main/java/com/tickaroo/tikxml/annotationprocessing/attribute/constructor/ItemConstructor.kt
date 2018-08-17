package com.tickaroo.tikxml.annotationprocessing.attribute.constructor

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
class ItemConstructor(
        @param:Attribute var aString: String?,
        @param:Attribute var anInt: Int,
        @param:Attribute var aBoolean: Boolean,
        @param:Attribute var aDouble: Double,
        @param:Attribute var aLong: Long,
        @param:Attribute(converter = DateConverter::class) var aDate: Date?,
        @param:Attribute var intWrapper: Int?,
        @param:Attribute var booleanWrapper: Boolean?,
        @param:Attribute var doubleWrapper: Double?,
        @param:Attribute var longWrapper: Long?
) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is ItemConstructor) return false

        val that = o as ItemConstructor?

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

package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
class EmptyDoublePropertyElement {
    @PropertyElement
    var empty: Double = 0.toDouble()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmptyDoublePropertyElement) return false

        val that = other as EmptyDoublePropertyElement?

        return java.lang.Double.compare(that!!.empty, empty) == 0
    }

    override fun hashCode(): Int {
        val temp = java.lang.Double.doubleToLongBits(empty)
        return (temp xor temp.ushr(32)).toInt()
    }
}

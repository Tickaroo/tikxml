package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
class EmptyLongPropertyElement {
    @PropertyElement
    var empty: Long = 0

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is EmptyLongPropertyElement) return false

        val that = other as EmptyLongPropertyElement?

        return empty == that!!.empty
    }

    override fun hashCode(): Int {
        return (empty xor empty.ushr(32)).toInt()
    }
}

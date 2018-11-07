package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
class EmptyBooleanPropertyElement {
    @PropertyElement
    var empty: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmptyBooleanPropertyElement) return false

        val that = other as EmptyBooleanPropertyElement?

        return empty == that!!.empty
    }

    override fun hashCode(): Int {
        return if (empty) 1 else 0
    }
}

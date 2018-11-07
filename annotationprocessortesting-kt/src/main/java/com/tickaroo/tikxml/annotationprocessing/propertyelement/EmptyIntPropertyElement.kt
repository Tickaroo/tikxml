package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
class EmptyIntPropertyElement {
    @PropertyElement
    var empty: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmptyIntPropertyElement) return false

        val that = other as EmptyIntPropertyElement?

        return empty == that!!.empty
    }

    override fun hashCode(): Int {
        return empty
    }
}

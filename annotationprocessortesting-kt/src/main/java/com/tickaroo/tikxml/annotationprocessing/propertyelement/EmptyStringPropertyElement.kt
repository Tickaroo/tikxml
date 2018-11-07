package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
class EmptyStringPropertyElement {
    @PropertyElement
    var empty: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmptyStringPropertyElement) return false

        val that = other as EmptyStringPropertyElement?

        return if (empty != null) empty == that!!.empty else that!!.empty == null
    }

    override fun hashCode(): Int {
        return if (empty != null) empty!!.hashCode() else 0
    }
}

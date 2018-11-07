package com.tickaroo.tikxml.annotationprocessing.elementlist

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.element.EmptyTag

/**
 * @author Hannes Dorfmann
 */
@Xml
class EmptyTagList {

    @Element
    var tags: List<EmptyTag>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmptyTagList) return false

        val that = other as EmptyTagList?

        return if (tags != null) tags == that!!.tags else that!!.tags == null
    }

    override fun hashCode(): Int {
        return if (tags != null) tags!!.hashCode() else 0
    }
}

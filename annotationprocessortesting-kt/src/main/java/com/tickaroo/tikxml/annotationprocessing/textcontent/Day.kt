package com.tickaroo.tikxml.annotationprocessing.textcontent

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Day {

    @TextContent
    var name: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Day) return false

        val day = other as Day?

        return if (name != null) name == day!!.name else day!!.name == null
    }

    override fun hashCode(): Int {
        return if (name != null) name!!.hashCode() else 0
    }
}

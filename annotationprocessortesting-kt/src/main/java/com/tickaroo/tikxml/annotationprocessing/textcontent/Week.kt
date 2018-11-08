package com.tickaroo.tikxml.annotationprocessing.textcontent

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Week {

    @Element
    var days: List<Day>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Week) return false

        val week = other as Week?

        return if (days != null) days == week!!.days else week!!.days == null
    }

    override fun hashCode(): Int {
        return if (days != null) days!!.hashCode() else 0
    }
}

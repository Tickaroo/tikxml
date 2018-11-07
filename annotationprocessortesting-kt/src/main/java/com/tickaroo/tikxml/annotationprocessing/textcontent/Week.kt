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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Week) return false

        val week = o as Week?

        return if (days != null) days == week!!.days else week!!.days == null
    }

    override fun hashCode(): Int {
        return if (days != null) days!!.hashCode() else 0
    }
}

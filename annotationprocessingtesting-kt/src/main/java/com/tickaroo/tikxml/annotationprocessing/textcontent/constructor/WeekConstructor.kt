package com.tickaroo.tikxml.annotationprocessing.textcontent.constructor

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "week")
class WeekConstructor(
        @param:Element val days: List<DayConstructor>?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WeekConstructor) return false

        val that = other as WeekConstructor?

        return if (days != null) days == that!!.days else that!!.days == null
    }

    override fun hashCode(): Int {
        return days?.hashCode() ?: 0
    }
}

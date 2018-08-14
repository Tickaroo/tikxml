package com.tickaroo.tikxml.annotationprocessing.textcontent.constructor

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "day")
class DayConstructor(@param:TextContent val name: String?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DayConstructor) return false

        val that = other as DayConstructor?

        return if (name != null) name == that!!.name else that!!.name == null
    }

    override fun hashCode(): Int {
        return name?.hashCode() ?: 0
    }
}

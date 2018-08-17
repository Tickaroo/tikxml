package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Boss : Person() {
    @PropertyElement
    var name: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Boss) return false
        if (!super.equals(other)) return false

        val boss = other as Boss?

        return if (name != null) name == boss!!.name else boss!!.name == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        return result
    }
}

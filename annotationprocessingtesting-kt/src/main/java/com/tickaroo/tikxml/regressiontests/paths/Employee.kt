package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
class Employee : Person() {
    @PropertyElement
    var name: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false
        if (!super.equals(other)) return false

        val employee = other as Employee?

        return if (name != null) name == employee!!.name else employee!!.name == null
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        return result
    }
}
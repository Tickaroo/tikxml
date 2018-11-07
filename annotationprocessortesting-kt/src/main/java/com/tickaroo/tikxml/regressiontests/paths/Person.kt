package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
open class Person {
    @PropertyElement
    var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        val person = other as Person?

        return id == person!!.id
    }

    override fun hashCode(): Int {
        return id
    }
}



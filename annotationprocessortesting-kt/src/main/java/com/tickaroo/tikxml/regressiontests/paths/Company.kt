package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Company {

    @Path("department/persons")
    @Element(typesByElement = [
        ElementNameMatcher(type = Person::class),
        ElementNameMatcher(type = Employee::class),
        ElementNameMatcher(type = Boss::class)
    ])
    var persons: List<@JvmSuppressWildcards Person>? = null

    @Path("department/emptyPersons")
    @Element(typesByElement = [
        ElementNameMatcher(type = Person::class, name="person1"),
        ElementNameMatcher(type = Employee::class),
        ElementNameMatcher(type = Boss::class)
    ])
    var emptyPersons: List<@JvmSuppressWildcards Person>? = null

    @Path("department") @Element
    var room: Room? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Company) return false

        val company = other as Company?

        return if (persons != null) persons == company!!.persons else company!!.persons == null
    }

    override fun hashCode(): Int {
        return if (persons != null) persons!!.hashCode() else 0
    }
}

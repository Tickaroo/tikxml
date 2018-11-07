package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "company")
data class CompanyDataClass(
        @field:Path("department/persons")
        @field:Element(typesByElement = [
            ElementNameMatcher(type = Person::class),
            ElementNameMatcher(type = EmployeeDataClass::class),
            ElementNameMatcher(type = BossDataClass::class)
        ])
        var persons: List<@JvmSuppressWildcards Person>? = null
)
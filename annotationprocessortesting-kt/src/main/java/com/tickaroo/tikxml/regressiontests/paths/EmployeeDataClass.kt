package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "employee")
data class EmployeeDataClass(
        @field:PropertyElement
        var name: String? = null
) : Person()
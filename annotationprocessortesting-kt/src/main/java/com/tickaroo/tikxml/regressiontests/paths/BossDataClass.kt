package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
data class BossDataClass(
        @field:PropertyElement
        var name: String? = null
) : Person()

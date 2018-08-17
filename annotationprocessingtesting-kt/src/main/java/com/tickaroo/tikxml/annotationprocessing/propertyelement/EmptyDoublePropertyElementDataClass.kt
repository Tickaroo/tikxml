package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
data class EmptyDoublePropertyElementDataClass(
        @field:PropertyElement
        var empty: Double = 0.toDouble()
)
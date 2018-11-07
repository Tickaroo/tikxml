package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "emptyPropertyTag")
data class EmptyStringPropertyElementDataClass(
        @field:PropertyElement
        var empty: String? = null
)